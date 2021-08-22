const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();
const db = admin.firestore();



exports.ameliyatbildirimiGonder=functions.firestore.document('randevular/{user_id}').onWrite(async (snap) =>{

    const user_id = snap.after.id;
    const oncekirandevutarihi = snap.before.data().randevuTarihi;
    const sonrakirandevutarihi = snap.after.data().randevuTarihi;

    if(oncekirandevutarihi != sonrakirandevutarihi){
        const token = admin.firestore().collection('users').doc(user_id).get()
        return token.then(doc=>{
            const fcmToken = doc.data().fcm_token;
            console.log("fcmfmcfmc   " , fcmToken);

            const randevuBildirimi = {
                notification : {
                    title : 'Randevunuz başarıyla bize ulaştı.',
                    body : 'Randevu isteğinizi aldık. En kısa zamanda sizi arayarak iletişime geçeceğiz',
                    icon : 'default'
                }
            };

            return admin.messaging().sendToDevice(fcmToken,randevuBildirimi).then(result => {
                console.log("Bildirim gönderildi");
            });
        })
    }
});

exports.scheduledFunctionCrontab = functions.pubsub.schedule('5 11 * * *')
  .timeZone('Asia/Jerusalem') 
  .onRun((context) => {
    const gunlukBildirim = {
        notification : {
            title : 'Değişmeden Değişin',
            body : 'Fonksiyon yoksa, güzel hissetmezsiniz',
            icon : 'default'
        }
    };

    return admin.messaging().sendToDevice(fcmToken,gunlukBildirim).then(result =>{
        console.log("Bildirim gönderildi");
    });
});

