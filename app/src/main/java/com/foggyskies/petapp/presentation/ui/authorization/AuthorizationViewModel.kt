package com.foggyskies.petapp.presentation.ui.registation

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.foggyskies.petapp.MainActivity.Companion.IDUSER
import com.foggyskies.petapp.MainActivity.Companion.TOKEN
import com.foggyskies.petapp.MainActivity.Companion.USERNAME
import com.foggyskies.petapp.PasswordCoder
import com.foggyskies.petapp.presentation.ui.navigationtree.NavTree
import com.foggyskies.petapp.routs.Routes
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.reflect.KSuspendFunction1

enum class StateAuthorization {
    SIGNIN, SIGNUP
}

@kotlinx.serialization.Serializable
data class RegistrationUserDC(
    var username: String,
    var password: String,
    var e_mail: String
)

@kotlinx.serialization.Serializable
data class LoginUserDC(
    var username: String,
    var password: String
)

class AuthorizationViewModel : ViewModel() {

    var top_label by mutableStateOf("Добро пожаловать!")

    var button_label by mutableStateOf("Войти")

    var button_change_state_label by mutableStateOf("У меня нет аккаунта")

    private var stateAuthorization by mutableStateOf(StateAuthorization.SIGNIN)

    var selectedTextField by mutableStateOf("")

    var error = mutableStateOf("")

    var login = mutableStateOf("")

    //    var telephoneNumber = mutableStateOf("")
    var email = mutableStateOf("")
    var password = mutableStateOf("")

    var list_fields = mapOf(
        "Email" to email,
        "Login" to login,
//        "Телефон" to telephoneNumber,
        "Пароль" to password
    )

    var list_labels by mutableStateOf(
        listOf("Login", "Пароль")
    )

//    val animation_error = derivedStateOf {
//        if (error.value != "") {
//            viewModelScope.launch {
//                delay(2000)
//                error.value = ""
//            }
//        }
//    }

    fun localCheckFields() {

        list_labels.forEach { key ->
            if (list_fields[key]?.value == "") {
                viewModelScope.launch {
                    throwError("Поле: \"$key\", не должно быть пустым")
                }
//                error.value =
//                animation_error
            }
        }
    }

    fun sendRequest(nav_controller: NavHostController, context: Context) {

        localCheckFields()

        if (error.value == "") {
            viewModelScope.launch {

                when (stateAuthorization) {
                    StateAuthorization.SIGNUP -> {

                        signUpRequest(
                            throwError = ::throwError,
                            context = context,
                            e_mail = email,
                            login = login,
                            password = password,
                            list_fields = list_fields,
                            nav_controller = nav_controller
                        )
                    }

                    StateAuthorization.SIGNIN -> {
                        signInRequest(
                            throwError = ::throwError,
                            context = context,
                            login = login,
                            password = password,
                            list_fields = list_fields,
                            nav_controller = nav_controller
                        )
                    }
                }
            }
        }
    }

    fun changeAuthState() {

        if (stateAuthorization == StateAuthorization.SIGNUP) {
            stateAuthorization = StateAuthorization.SIGNIN
            list_labels = listOf("Login", "Пароль")
            top_label = "Добро пожаловать!"
            button_label = "Войти"
            button_change_state_label = "У меня нет аккаунта"
        } else {
            stateAuthorization = StateAuthorization.SIGNUP
            list_labels = listOf("Email", "Login", "Пароль")
            top_label = "Присоединяйся к нам!"
            button_label = "Зарегистрироваться"
            button_change_state_label = "Вернуться ко входу"
        }
    }

    suspend fun throwError(errorDescription: String) {
        error.value = errorDescription
        delay(2000)
        error.value = ""
    }
}

private suspend fun signUpRequest(
    throwError: KSuspendFunction1<String, Unit>,
    context: Context,
    e_mail: MutableState<String>,
    login: MutableState<String>,
    password: MutableState<String>,
    list_fields: Map<String, MutableState<String>>,
    nav_controller: NavHostController
) {
    HttpClient(Android) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
//        install(ContentNegotiation){
//            json(Json {
//                prettyPrint = true
//                isLenient = true
//            })
//        }
        expectSuccess = false
        install(HttpTimeout) {
            requestTimeoutMillis = 3000
        }
    }.use {
//                            val responseRegistration =
        val response: HttpResponse =
            it.post("${Routes.SERVER.REQUESTS.BASE_URL}/registration") {
                headers {
                    append("Content-Type", "Application/Json")
                }
                body = (
                    RegistrationUserDC(
                        username = login.value,
                        e_mail = e_mail.value,
                        password = PasswordCoder.encodeStringFS(password.value)
                    )
                )
            }
        if (response.status.isSuccess()) {
            signInRequest(
                throwError = throwError,
                context = context,
                login = login,
                password = password,
                list_fields = list_fields,
                nav_controller = nav_controller
            )
        } else {
            throwError(response.readText())
        }
    }
}

suspend fun signInRequest(
    throwError: KSuspendFunction1<String, Unit>? = null,
    context: Context,
    login: MutableState<String>,
    password: MutableState<String>,
    list_fields: Map<String, MutableState<String>>,
    nav_controller: NavHostController
) {
    HttpClient(Android) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
//        install(ContentNegotiation){
//            json(Json {
//                prettyPrint = true
//                isLenient = true
//            })
//        }
        expectSuccess = false
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
        }
    }.use {
        val response: HttpResponse = it.post("${Routes.SERVER.REQUESTS.BASE_URL}/auth") {
            headers {
                append("Content-Type", "Application/Json")
            }
            body = (
                LoginUserDC(
                    username = login.value,
                    password = PasswordCoder.encodeStringFS(password.value)
                )
            )
        }
        if (!response.status.isSuccess()) {
            if (throwError != null)
                throwError(response.readText())
            else {

            }
        } else {
            authorization_save(
                context,
                login.value,
                PasswordCoder.encodeStringFS(password.value),
                token = response.readText().replace("\"", "")
            )
            CoroutineScope(Dispatchers.Main).launch {
                list_fields.forEach {
                    it.value.value = ""
                }
                nav_controller.navigate(NavTree.Home.name)
            }
        }
    }
}

fun authorization_save(
    applicationContext: Context,
    username: String,
    password: String,
    token: String
) {
    val values = token.split("|")
    applicationContext.getSharedPreferences(
        "Token",
        Context.MODE_PRIVATE
    )
        .edit()
        .putString("Token", values[0])
        .apply()
    applicationContext.getSharedPreferences(
        "User",
        Context.MODE_PRIVATE
    )
        .edit()
        .putString("username", username)
        .putString("password", password)
        .putString("idUser", values[1])
        .apply()
    TOKEN = values[0]
    USERNAME = username
    IDUSER = values[1]
}

fun logOut(
    applicationContext: Context
) {
    applicationContext.getSharedPreferences(
        "Token",
        Context.MODE_PRIVATE
    )
        .edit()
        .putString("Token", "")
        .apply()
    applicationContext.getSharedPreferences(
        "User",
        Context.MODE_PRIVATE
    )
        .edit()
        .putString("username", "")
        .putString("password", "")
        .apply()
    TOKEN = ""
    USERNAME = ""
}