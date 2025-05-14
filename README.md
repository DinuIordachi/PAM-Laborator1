# Raport de Proiect Android – Kotlin

## Titlul proiectului: Task Manager UI

**Tehnologii utilizate:**  
- Kotlin  
- Android SDK  
- ViewModel + LiveData  
- RecyclerView  
- ViewBinding  
- Sistem de notificări  
- Material Design

---

## 1. Descriere generală

Acest proiect reprezintă o aplicație Android scrisă în limbajul Kotlin, care simulează un mini sistem de gestionare a sarcinilor (**Task Manager**). Utilizatorul poate introduce un cuvânt cheie, efectua o căutare Google, adăuga o sarcină, șterge lista de sarcini și programa o notificare ce apare peste 10 secunde.

---

## 2. Obiective implementate

- ✅ Interfață UI compusă din:
  - 4 butoane (`Search`, `Add`, `Clear`, `Notify`)
  - 1 câmp de text (`EditText`)
  - 1 listă personalizată (`RecyclerView`) pentru afișarea obiectelor `Task`

- ✅ Căutare Google în browser folosind `Intent.ACTION_VIEW`
- ✅ Adăugare și ștergere sarcini cu ajutorul unui `ViewModel`
- ✅ Notificare programată după 10 secunde, cu verificare a permisiunii
- ✅ Cerere dinamică a permisiunii `POST_NOTIFICATIONS` pentru Android 13+

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

---

## 8. Bibliografie

- [developer.android.com](https://developer.android.com)
