# Raport
Lucrare de laborator nr.1 *la Programarea aplicatiilor Mobile*

**A efectuat:**	st. gr. TI-211 F/r Iordachi Dumitru

**A verificat:** prof. univ. Antohi Ionel


# Tema: UI View Model

## Obiective 
Dezvoltarea unei aplicații pe una din platformele stabilite la Laboratorul Nr.0 , utilizând mediul de dezvoltare corespunzător acesteia. 

## Scopul 
De prezentat o aplicație ce rulează pe un dispozitiv sau emulator, ce va conține pe interfața sa, următoarele elemente: 
1.  4 butoane (ce vor executa condițiile de mai jos) 
2.	1 TextBox (pentru input) 
3.	1 custom Object List (pentru afisarea unei liste de elemente conform cerintei de mai jos)
 
## Condiții 

De utilizat componentele UI pentru a realiza următoarele condiții: 
1.	De creat un push notification pe ecranul dispozitivului care se va trata peste 10s. 
2.	De utilizat browserul intern al dispozitivului, pentru a inițializa o căutare în Google, conform cuvântului cheie introdus în TextBox. 
3.	Pentru elementul de tip List Option/Entity View se va alege o tema la dorinta propie. Conditiile pe care trebuie sa le intruneasca elemnetele afisate in cadrul aplicatie sunt urmatoarele:
    - Head Title (se va defini numele general al obiectului)
    -	Content Option (continutul mai desfasurat al elementului afisat)  

---

# Mersul Lucrării

## 1. Notificarea

```kotlin
object NotificationUtil {
    private const val CHANNEL_ID = "task_channel"

    fun showNotification(context: Context) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Task Channel", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Notificare Programată")
            .setContentText("Aceasta este o notificare după 10 secunde")
            .setSmallIcon(R.drawable.ic_bell)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(12345, builder.build())
    }
}

```

---

## 3. Funcționalități detaliate

### a. Căutare Google
Utilizatorul poate introduce un cuvânt în câmpul `EditText`, iar prin apăsarea butonului `Search`, aplicația lansează un browser care deschide căutarea Google cu acel cuvânt.

### b. Adăugare sarcini
Prin butonul `Add`, se adaugă în listă un obiect `Task` cu titlu și descriere generate pe baza textului introdus.

### c. Ștergere sarcini
Butonul `Clear` golește întreaga listă observabilă din `ViewModel`.

### d. Notificări
Aplicația lansează o notificare după 10 secunde de la apăsarea butonului `Notify`. Dacă permisiunea nu este acordată, aplicația solicită permisiunea în mod prietenos, cu explicații (`rationale`) și trimitere în setări dacă este necesar.

---

## 4. Structura aplicației

- `MainActivity.kt`: gestionează interfața și evenimentele UI
- `Task.kt`: model de date simplu cu `title` și `description`
- `TaskViewModel.kt`: folosește `LiveData` pentru lista de sarcini
- `TaskAdapter.kt`: adaptează lista de sarcini în `RecyclerView`
- `NotificationUtil.kt`: conține logica pentru afișarea notificării

---

## 5. Probleme întâmpinate

- Necesitatea gestionării corecte a permisiunilor de notificare pentru Android 13+
- Utilizarea `applicationContext` în loc de `Activity context` ducea la erori de tip `uid -1`
- Adaptarea cererii de permisiune la standardele Android moderne (`ActivityResultLauncher`)

---

## 6. Concluzii

Proiectul a atins toate obiectivele propuse și demonstrează integrarea mai multor componente importante din ecosistemul Android:

- Arhitectură MVVM (Model-View-ViewModel)
- Componente reactive (`LiveData`)
- Gestionarea permisiunilor la runtime
- Utilizarea notificărilor și `MaterialAlertDialog`

---

## 7. Vizualizare

![](./pam-laborator1.GIF)

---

## 8. Bibliografie

- [developer.android.com](https://developer.android.com)
