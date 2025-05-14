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

## 2. Căutarea Google în browser

```kotlin
btnSearch.setOnClickListener {
    val query = searchInput.text.toString()
    if (query.isNotEmpty()) {
        val url = "https://www.google.com/search?q=${Uri.encode(query)}"
        startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }
}
```

## 3. Lista de elemente

### 3.1. XML Layout
Am definit o temă proprie care constă dintr-un Linear Layout vertical. Lyout-ul conține 2 TextView pentru textul si descrierea task-ului respectiv. Fiecare Task va fi inculs în lista `RecyclerView` declarată în `activiy_main.xml`
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:padding="8dp"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <TextView
        android:id="@+id/taskTitle"
        android:textStyle="bold"
        android:textSize="18sp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

    <TextView
        android:id="@+id/taskDescription"
        android:textSize="14sp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
</LinearLayout>
```

```xml
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/recyclerView"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:layout_weight="1" />
```

### 3.2. TaskViewModel
Am utilizat `TaskViewModel` pentru a gestiona logic datele într-un mod separat față de interfața grafică. Acesta păstrează și expune o listă de sarcini (`LiveData<List<Task>>`) care poate fi observată din activitate, astfel încât UI-ul să se actualizeze automat la modificări, fără a pierde datele în cazul rotației ecranului.

```kotlin
class TaskViewModel : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>(emptyList())
    val tasks: LiveData<List<Task>> = _tasks

    fun addTask(task: Task) {
        _tasks.value = _tasks.value?.plus(task)
    }

    fun clearTasks() {
        _tasks.value = emptyList()
    }
}
```

### 3.3. TaskAdapter
Am utilizat `TaskAdapter` pentru a afișa lista de obiecte `Task` într-un `RecyclerView` într-un mod eficient și optimizat. Acest adapter extinde `ListAdapter`, care folosește `DiffUtil` pentru a actualiza doar elementele care s-au modificat, oferind performanță mai bună și o experiență fluentă în interfața grafică.

```kotlin
class TaskAdapter : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.taskTitle)
        private val description = itemView.findViewById<TextView>(R.id.taskDescription)

        fun bind(task: Task) {
            title.text = task.title
            description.text = task.description
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.title == newItem.title
        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }
}
```

### 3.4. Adăugare și ștergere

```kotlin
btnAdd.setOnClickListener {
    val query = searchInput.text.toString()
    viewModel.addTask(Task(title = query, description = "Descriere pentru $query"))
}

btnClear.setOnClickListener {
    viewModel.clearTasks()
}
```
---

## 4. Funcționalități adiționale
Pentru a asigura funcționarea corectă a notificărilor am adăugat solicitarea permisiunilor de notificare direct din aplicație ținând cont de versiunile Android API.

```kotlin
private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasNotificationPermissionGranted = isGranted
            if (!isGranted) {
                // If permission not granted and it's Android API >= 33, show rationale materialUI
                if (Build.VERSION.SDK_INT >= 33) {
                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                        showNotificationPermissionRationale()
                    } else {
                        // if Android API < 33 show classic dialog that redirects to settings
                        showSettingDialog()
                    }
                }
            } else {
                Toast
                    .makeText(this, "notification permission granted", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Notification Permission")
            .setMessage("Notification permission is required, Please allow notification permission from setting")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = "package:$packageName".toUri()
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showNotificationPermissionRationale() {
        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Alert")
            .setMessage("Notification permission is required, to show notification")
            .setPositiveButton("Ok") { _, _ ->
                launchNotificationPermissions()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun launchNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ActivityCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS")
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Trigger permission request from caller
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)

                return
            }
        } else {
            hasNotificationPermissionGranted = true
        }
    }
```
---

## Concluzie
Lucrarea de față a demonstrat realizarea unei aplicații Android utilizând limbajul Kotlin și principiile moderne ale arhitecturii MVVM (Model-View-ViewModel). Aplicația „Task Manager UI” îmbină funcționalități esențiale din dezvoltarea Android, precum manipularea interfeței grafice prin ViewBinding, afișarea de liste dinamice cu ajutorul RecyclerView și actualizarea automată a datelor prin LiveData. În plus, s-a implementat un sistem complet de notificări, inclusiv gestionarea permisiunilor POST_NOTIFICATIONS în conformitate cu cerințele Android 13+.
Proiectul a urmărit nu doar realizarea funcționalităților de bază (adăugare, ștergere, notificări), ci și o abordare corectă și scalabilă din punct de vedere al codului sursă. Utilizarea ViewModel-ului a permis separarea clară dintre logică și interfață, făcând aplicația mai ușor de întreținut. De asemenea, integrarea permisiunilor runtime și afișarea de mesaje explicative (rationale dialogs) a adus aplicația la un nivel profesional, respectând bunele practici Android.
În concluzie, acest proiect oferă o bază solidă pentru aplicații mai complexe, demonstrând că principii simple și clare pot duce la rezultate eficiente și bine structurate.

---

## 7. Vizualizare

![](./pam-laborator1.GIF)

---

## 8. Bibliografie

- [developer.android.com](https://developer.android.com)
