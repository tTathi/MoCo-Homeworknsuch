package com.example.mchw1app

//import androidx.compose.material3.Scaffold
//import java.util.jar.Manifest
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
//import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import coil.compose.rememberAsyncImagePainter
import com.example.mchw1app.ui.theme.MCHW1AppTheme
import kotlinx.serialization.Serializable
import java.io.File
import java.io.FileOutputStream
import androidx.core.app.RemoteInput
import android.hardware.SensorManager
import android.hardware.Sensor


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        val sense = SensorActivity(this)
        sense.init()

        setContent {

            MCHW1AppTheme {
                createNotificationChannel(LocalContext.current)

                when {
                    ContextCompat.checkSelfPermission(
                        LocalContext.current,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        // You can use the API that requires the permission.
                    }
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.POST_NOTIFICATIONS) -> {
                        // In an educational UI, explain to the user why your app requires this
                        // permission for a specific feature to behave as expected, and what
                        // features are disabled if it's declined. In this UI, include a
                        // "cancel" or "no thanks" button that lets the user continue
                        // using your app without granting the permission.
                    }
                    else -> {
                        // You can directly ask for the permission.
                        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            0)
                    }
                }

                NavigationBegin()
            }

        }
    }
}

//data class Message(val author: User, val body: String)

@Entity
data class User(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "last_name") val lastName: String?,
    @ColumnInfo(name = "URI") val picURI: String
)

@Entity
data class Message(
    @PrimaryKey val uid: Int,
    @ColumnInfo("author") val authorId: Int,
    @ColumnInfo("content") val content: String
)

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE uid LIKE :userID Limit 1")
    fun loadByID(userID: Int): User

    @Query("SELECT * FROM User WHERE first_name LIKE :first AND last_name LIKE :last Limit 1")
    fun findByName(first: String, last: String): User

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)

    @Query("Update User set URI = :uri where uid = :id")
    fun changePicture(uri: String, id: Int)
}

@Dao
interface MessageDao {

    @Query("SELECT * FROM Message")
    fun getAll(): List<Message>

    @Insert
    fun insertAll(vararg messages: Message)
}

@Database(entities = [User::class, Message::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
}

@Composable
fun MessageCard(msg: Message, userDao: UserDao) {

    Row {
        Image(
            painter = rememberAsyncImagePainter(userDao.loadByID(msg.authorId).picURI),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                .size(40.dp) // Set image size to 40 dp
                .clip(CircleShape) // Clip image to be shaped as a circle
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        var isExpanded by remember { mutableStateOf(false) }

        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        )

        Column(modifier = Modifier.clickable { isExpanded = !isExpanded })
        {
            Text(
                text = "${userDao.loadByID(msg.authorId).firstName} ${userDao.loadByID(msg.authorId).lastName}",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            )
            {
                Text(
                    text = msg.content,
                    modifier = Modifier.padding(all = 4.dp),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview (
    showBackground = true,
    name = "Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun PreviewMessageCard() {

    MCHW1AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            /*
            MessageCard(
                msg = Message("Tathi", "Lolzista ja trolloloo myös, mutta törkeää ei.")
            )
            */
        }
    }
}

@Serializable
object FaceConversation
@Serializable
object LostScreen

private fun createNotificationChannel(theContext: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        val name = "OneChannelToRuleThemAll"
        val descriptionText = "This is THE channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("Channelington", name, importance).apply { description = descriptionText }

        val notificationManager: NotificationManager = getSystemService(theContext,
            NotificationManager::class.java) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}


private fun sendNotification(theContext: Context, noteID: Int, bob: NotificationCompat.Builder) {

    with(NotificationManagerCompat.from(theContext)) {
        if (ActivityCompat.checkSelfPermission(
                theContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return@with
        }
        // notificationId is a unique int for each notification that you must define.
        notify(noteID, bob.build())
    }
}
val KEY_TEXT_REPLY = "keytextreply"
private fun getMessageReplyIntent(conversationId: Int): Intent {
    return Intent()
        .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        .setAction("com.example.android.messagingservice.ACTION_MESSAGE_REPLY")
        .putExtra("converseID", conversationId)
}

private fun getMessageText(intent: Intent): CharSequence? {
    return android.app.RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_TEXT_REPLY)
}

private lateinit var sensorManager: SensorManager


@Composable
fun NavigationBegin() {

    val navController = rememberNavController()

    val db = Room.databaseBuilder(
        LocalContext.current,
        AppDatabase::class.java, "User-Database"
    ).allowMainThreadQueries().build()

    val userDao = db.userDao()
    val messageDao = db.messageDao()

    val intent = Intent(LocalContext.current, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(LocalContext.current, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    var remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
        setLabel("Insert reply")
        build()
    }

    val replyPendingIntent: PendingIntent =
        PendingIntent.getBroadcast(LocalContext.current,
            3,
            getMessageReplyIntent(3),
            PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_MUTABLE + PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT)

    var action: NotificationCompat.Action = NotificationCompat.Action.Builder(
        R.drawable.ic_launcher_foreground,
        "Labello",
        replyPendingIntent
    ).addRemoteInput(remoteInput).build()

    var builder = NotificationCompat.Builder(LocalContext.current, "Channelington")
        .setSmallIcon(R.drawable.ic_launcher_foreground) //
        .setContentTitle("We miss you")
        .setContentText("Please return. We cannot live without you. We love you dearly")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .addAction(action)

    sendNotification(LocalContext.current, 1, builder)

    if (userDao.getAll().count() == 0) {

        val uri1 = Uri.parse("android.resource://com.example.mchw1app/drawable/small_naama")
        val uri2 = Uri.parse("android.resource://com.example.mchw1app/drawable/androidkuva")

        userDao.insertAll(
            User(
                uid = 0,
                firstName = "Tat",
                lastName = "Hi",
                picURI = uri1.toString()
            ),
            User(
                uid = 1,
                firstName = "Sample",
                lastName = "Name",
                picURI = uri2.toString()
        )
        )
    }
    if (messageDao.getAll().count() == 0) {

        val number = messageDao.getAll().count()

        messageDao.insertAll(
            Message(
                number,
                0,
                "Hello!"
            ),
            Message(
                number + 1,
                0,
                "Type in the text field and click send to add your own message"
            ),
            Message(
                number + 2,
                0,
                "If you go somewhere, you can change your picture"
            ),
        )
    }

    NavHost(
        navController = navController,
        startDestination = FaceConversation

    ) {
        composable<FaceConversation> {

            Conversation(
                onNavigateToSomewhere = {
                    navController.navigate(route = LostScreen)
                },
                onAddMSG = {
                    navController.navigate(route = FaceConversation) {
                        popUpTo(FaceConversation) {
                            inclusive = true
                        }
                    }
                },
                userDao = userDao,
                messageDao = messageDao
            )

        }
        composable<LostScreen> {
            OtherScreen(
                onNavigateBack = {
                    navController.navigate(route = FaceConversation) {
                        popUpTo(FaceConversation) {
                            inclusive = true
                        }
                    }
                },
                onPickIMG = {
                    navController.navigate(route = LostScreen) {
                        popUpTo(LostScreen) {
                            inclusive = true
                        }
                    }
                },
                userDao = userDao,
                messageDao = messageDao
            )
        }
    }

}

@Composable
fun Conversation(onNavigateToSomewhere: () -> Unit, onAddMSG: () -> Unit, userDao: UserDao, messageDao: MessageDao) {

    //val sense = SensorActivity()
    //sense.stopSensor()

    var text by remember { mutableStateOf("") }

    Column() {

        Spacer(modifier = Modifier.height(4.dp))

        Row() {


            FaceLand()
            Spacer(modifier = Modifier.width(90.dp))
            Button(onClick = {onNavigateToSomewhere()}) {
                Text("To somewhere")
            }
        }

        val messages = messageDao.getAll()

        LazyColumn(
            modifier = Modifier.height(600.dp)
        )
        {
            items(messages) { message ->
                MessageCard(message, userDao)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row() {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Label") }
            )
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
                    .clickable() {
                        messageDao.insertAll(
                            Message(
                                uid = messageDao.getAll().count(),
                                authorId = 1,
                                content = text
                            )
                        )
                        onAddMSG()
                    }
                    .height(55.dp)
                    .width(55.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Send",
                    textAlign = TextAlign.Center,
                    lineHeight = 3.5.em
                )
            }
        }
    }
}

@Composable
fun OtherScreen(onNavigateBack: () -> Unit, onPickIMG: () -> Unit, userDao: UserDao, messageDao: MessageDao) {



    val context = LocalContext.current

    val pickMedia = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) {
            uri ->
        if (uri != null) {
            Log.d("Photopicker", "Selected URI: $uri. Absolute: ${uri.isAbsolute}")

            val inputStream = context.contentResolver.openInputStream(uri)
            val outputFile = File(context.filesDir, "user_avatar.jpg")

            FileOutputStream(outputFile).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            userDao.changePicture( outputFile.absolutePath, 1)
            onPickIMG()
        } else
        {
            Log.d("Photopicker", "No media selected")
        }
    }

    //sensorManager = getSystemService(LocalContext.current, SensorManager::class.java) as SensorManager


    /*
    val gravSensor = if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
        //Log.d("Sensors", "Found gravity sensor")
        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    }
    else {
        Log.d("Sensors", "No gravity sensor found")
        null
    }
    */


    //gravSensor.

    Surface(modifier = Modifier.fillMaxSize()) {
        Column() {
            MessageCard(
                Message(
                    messageDao.getAll().count(),
                    1,
                    content = "Heehoo",
                ),
                userDao = userDao
            )
            Button(onClick = {onNavigateBack()}) {
                Text("Back")
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(onClick = {
                pickMedia.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }) {
                Text("Choose Profile Picture")
            }

        }

    }


}

@Composable
fun FaceLand() {

    var isSmiling by remember { mutableStateOf(true) }

    Image(
        painter =
            if (isSmiling) painterResource(R.drawable.smile)
            else painterResource(R.drawable.frown),
        contentDescription = "Contact profile picture",
        modifier = Modifier
            .size(160.dp) // Set image size to 160 dp
            .clip(CircleShape) // Clip image to be shaped as a circle
            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
            .clickable { isSmiling = !isSmiling },
    )
}

@Preview
@Composable
fun PreviewConversation() {
    MCHW1AppTheme {
            NavigationBegin(
                /*
                listOf(
                    Message(
                        "Tathi",
                        "Hello! Can anyone seee this?"
                    ),
                    Message(
                        "Tathi",
                        "If so, I need you to do something for me"
                    ),
                    Message(
                        "Tathi",
                        """I'm being watched though, so I can't give the instructions immediately"""
                    ),
                    Message(
                        "Tathi",
                        "Be patient\nPlease\ntap\nthe\nsmiling\nface"
                    ),
                    Message(
                        "Tathi",
                        """Hey, take a look at Jetpack Compose, it's great!
            |It's the Android's modern toolkit for building native UI.
            |It simplifies and accelerates UI development on Android.
            |Less code, powerful tools, and intuitive Kotlin APIs :)""".trim()
                    ),
                    Message(
                        "Tathi",
                        "Sorry, you can ignore that. I had to pretend to do some actual work"
                    ),
                    Message(
                        "Tathi",
                        "Okay, the next message will have important information, make sure no one is watching"
                    ),
                    Message(
                        "Tathi",
                        "Here goes: Expand the message telling you to be patient."
                    ),
                    Message(
                        "Tathi",
                        "Did you do it?"
                    ),
                    Message(
                        "Tathi",
                        "If you did, MWAHAHAHAHHAHAA! You made the smiley sad! You're now evil!"
                    ),
                    Message(
                        "Tathi",
                        "And don't go tapping the face again"
                    ),
                    Message(
                        "Tathi",
                        "...You fixed your mistake, didn't you..."
                    ),
                    Message(
                        "Tathi",
                        "My plan to make the face sad has been spoiled :("
                    ),
                )
                 */
            )
    }
}