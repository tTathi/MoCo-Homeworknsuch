package com.example.mchw1app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
//import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mchw1app.ui.theme.MCHW1AppTheme
import androidx.compose.ui.res.painterResource
import android.content.res.Configuration
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.material3.Button
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        setContent {
            MCHW1AppTheme {
                NavigationBegin(
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
                )
            }

        }
    }
}



data class Message(val author: String, val body: String)

@Entity
data class User(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "last_name") val lastName: String?
)

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)
}

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

@Composable
fun MessageCard(msg: Message) {

    Row {
        Image(
            painter = painterResource(R.drawable.small_naama),
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
                text = "${msg.author}",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier.animateContentSize().padding(1.dp)
            )
            {
                Text(
                    text = msg.body,
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

            MessageCard(
                msg = Message("Tathi", "Lolzista ja trolloloo myös, mutta törkeää ei.")
            )
        }
    }
}

@Serializable
object FaceConversation
@Serializable
object LostScreen



@Composable
fun NavigationBegin(messages: List<Message>) {

    val navController = rememberNavController()


    val db = Room.databaseBuilder(
        LocalContext.current,
        AppDatabase::class.java, "User-Database"
    ).build()



    //val userDao = db.userDao()
    /*
    userDao.insertAll(
        User
            (
                uid = 123,
                firstName = "Test",
                lastName = "McTestface",
            )
    )
    */

    NavHost(
        navController = navController,
        startDestination = FaceConversation
    ) {
        composable<FaceConversation> {

            Conversation(
                messages = messages,
                onNavigateToSomewhere = {
                    navController.navigate(route = LostScreen)
                }
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
                //nummer = userDao.getAll().count()
            )
        }
    }

}



@Composable
fun Conversation(messages: List<Message>, onNavigateToSomewhere: () -> Unit,) {

    Column() {

        Row() {
            FaceLand()
            Spacer(modifier = Modifier.width(90.dp))
            Button(onClick = {onNavigateToSomewhere()}) {
                Text("To somewhere")
            }
        }

        LazyColumn() {

            items(messages) { message ->
                MessageCard(message)
            }
        }
    }
}

@Composable
fun OtherScreen(onNavigateBack: () -> Unit/*, nummer: Int*/) {



    Surface(modifier = Modifier.fillMaxSize()) {
        Column() {
            MessageCard(Message(
                "Tathi",
                "You may be lost...",
            ),
            )
            Button(onClick = {onNavigateBack()}) {
                Text("Back where you came from")
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
            )
    }
}