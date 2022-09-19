package com.foggyskies.petapp.presentation.ui.chat.requests

import androidx.compose.ui.platform.AndroidUiDispatcher.Companion.Main
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.chat.ChatViewModel
import com.foggyskies.petapp.presentation.ui.chat.EditMessageEntity
import com.foggyskies.petapp.presentation.ui.chat.StateTextField
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.BASE_URL
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.ChatRoute.EDIT_MESSAGE
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun ChatViewModel.editMessage() {
    backgroundScope.launch {
        checkInternet(
            request = {
                val message = EditMessageEntity(
                    idUser = if (messageSelected?.idUser!! == MainActivity.IDUSER)
                        chatEntity?.idCompanion!!
                    else
                        MainActivity.IDUSER,
                    idChat = chatEntity?.id!!,
                    idMessage = messageSelected?.id!!,
                    newMessage = bottomBarValue
                )
                cRequest<Unit>(
                    response = editMessageRequest(message),
                    onOk = {
                        repositoryUserDB.editMessage(
                            chatEntity?.id!!,
                            messageSelected?.id!!,
                            bottomBarValue
                        )
                        withContext(Main){
                            _state[_state.indexOf(messageSelected)].message = bottomBarValue
                            messageSelected = null
                            bottomBarValue = lastBottomBatValue
                            stateTextField =
                                if (lastBottomBatValue.isEmpty()) StateTextField.EMPTY else StateTextField.WRITING
                        }
                    }
                )
            }
        )
    }
}

suspend fun editMessageRequest(message: EditMessageEntity): HttpResponse {
    clientJson.use {
        return it.post(BASE_URL + EDIT_MESSAGE) {
            contentType(ContentType.Application.Json)
            header(BuildConfig.Authorization, MainPreference.Token)
            setBody(message)
        }
    }
}