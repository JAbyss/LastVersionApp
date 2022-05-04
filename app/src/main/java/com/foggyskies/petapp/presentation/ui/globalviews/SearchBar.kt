package com.foggyskies.petapp.presentation.ui.globalviews

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.home.HomeMVIModel
import com.foggyskies.petapp.presentation.ui.home.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("MutatingSharedPrefs")
@Composable
fun SearchBar(msViewModel: MainSocketViewModel) {

    val focus_manager = LocalFocusManager.current

    val keyboard_manager = LocalSoftwareKeyboardController.current

    val scope = rememberCoroutineScope()

    val search_value = remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    val USER_VALUES = "user_values"

    Column() {

        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 18.dp)
                .shadow(3.dp, shape = RoundedCornerShape(20.dp), clip = true)
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth(1f)
                .background(Color.White)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .fillMaxWidth()
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .weight(1f),
                    Color(0xFF898989)
                )

                TextField(
                    value = search_value.value,
                    onValueChange = {
                        search_value.value = it
                        if (search_value.value.isBlank()) {
                            scope.launch {
                                delay(3000)
                                if (search_value.value.isBlank()) {
                                    focus_manager.clearFocus()
                                }
                            }
                        } else {
                            msViewModel.sendMessage(search_value.value)
                            }
                    },
                    label = {
                        Text(
                            text = "Кого ищем?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 14.sp
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {

                            context.getSharedPreferences("search", Context.MODE_PRIVATE)
                                .getString(USER_VALUES, "").let { string ->

                                    when {
                                        string!!.isEmpty() ->
                                            context.getSharedPreferences(
                                                "search",
                                                Context.MODE_PRIVATE
                                            )
                                                .edit()
                                                .putString(
                                                    USER_VALUES,
                                                    "\"${search_value.value}\""
                                                )
                                                .apply()
                                        string.split("\" ").size < 5 -> {

                                            context.getSharedPreferences(
                                                "search",
                                                Context.MODE_PRIVATE
                                            )
                                                .edit()
                                                .putString(
                                                    USER_VALUES,
                                                    "$string \"${search_value.value}\""
                                                )
                                                .apply()
                                        }
                                        else -> {
                                            val list = string.split(regex = ".(?<=\"\\s)".toRegex())
                                            val splited = list[0]
                                            val formated_sting = string.replace(splited, "")
                                            context.getSharedPreferences(
                                                "search",
                                                Context.MODE_PRIVATE
                                            )
                                                .edit()
                                                .putString(
                                                    USER_VALUES,
                                                    "$formated_sting \"${search_value.value}\""
                                                )
                                                .apply()
                                        }
                                    }

                                }
                            keyboard_manager?.hide()
                        }
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    modifier = Modifier
                        .weight(8f)
                )
            }
        }

        val list_tag = mutableListOf<String>()

        context.getSharedPreferences("search", Context.MODE_PRIVATE)
            .getString(USER_VALUES, "")
            .let { mutable_set ->
                if (mutable_set?.isNotEmpty() == true)
                    list_tag.addAll(mutable_set!!.split(regex = ".(?<=\"\\s)".toRegex()))
            }
    }
}