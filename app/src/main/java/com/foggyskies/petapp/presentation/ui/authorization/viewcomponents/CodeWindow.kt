package com.foggyskies.petapp.presentation.ui.authorization.viewcomponents

import android.content.Context.CLIPBOARD_SERVICE
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.foggyskies.petapp.PasswordCoder
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.authorization.AuthorizationViewModel
import com.foggyskies.petapp.presentation.ui.authorization.models.RegistrationUserWithCodeDC
import com.foggyskies.petapp.presentation.ui.authorization.requests.registration
import com.foggyskies.petapp.presentation.ui.navigationtree.NavTree
import kotlin.collections.set

@Composable
fun BoxScope.CodeWindow(viewModel: AuthorizationViewModel, nav_controller: NavHostController) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .fillMaxWidth(0.7f)
            .align(Center)
            .background(Color(0xCC382424))
    ) {

        Column(
            modifier = Modifier
                .align(Center)
        ) {

            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = "Код подтверждения",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(7.dp))

            CodeInput(
                modifier = Modifier
                    .padding(vertical = 7.dp)
                    .fillMaxWidth(0.9f)
                    .align(CenterHorizontally)
            ) { code ->
                val data = RegistrationUserWithCodeDC(
                    username = viewModel.login.value,
                    password = PasswordCoder.encodeStringFS(viewModel.password.value),
                    e_mail = viewModel.email.value,
                    code = code
                )
                viewModel.registration(data) {
                    nav_controller.navigate(NavTree.Home.name)
                }
            }
        }
    }
}

@Composable
fun CodeInput(
    lengthCode: Int = 4,
    shape: Shape = RoundedCornerShape(10.dp),
    weightItem: Float = 1f / lengthCode,
    weightSpacer: Float = weightItem / 2,
    modifierOneCodeItem: Modifier = Modifier
        .clip(shape)
        .background(Color(0xFF9B9C9D))
        .defaultMinSize(minHeight = 40.dp),
    modifier: Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    action: (String) -> Unit
) {
    val context = LocalContext.current.applicationContext
    val focusManager = LocalFocusManager.current
    var isBuffered by remember {
        mutableStateOf(false)
    }
    var lockBuffer by remember {
        mutableStateOf(false)
    }
    val regex = "^[A-z0-9]{$lengthCode}\$".toRegex()
    val fullCodeMap = remember {
        mutableStateMapOf<Int, String>()
    }

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    repeat(lengthCode) {
                        fullCodeMap[it] = ""
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    focusManager.clearFocus()
                }
                else -> {}
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)
        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Row(
        modifier = modifier
    ) {

        repeat(lengthCode) { code_item ->

            Box(
                contentAlignment = Alignment.Center,
                modifier = modifierOneCodeItem
                    .weight(weightItem)
            ) {

                val value = fullCodeMap[code_item].toString()

                BasicTextField(
                    value = value,
                    onValueChange = {
                        if (it.length < fullCodeMap[code_item]!!.length) {
                            fullCodeMap[code_item] = ""
                            focusManager.moveFocus(FocusDirection.Left)
                        }
                        if (it.length == 1) {
                            fullCodeMap[code_item] = it
                            if (code_item + 1 == lengthCode) {
                                focusManager.clearFocus()
                                action(getString(fullCodeMap))
                            } else
                                if (getString(fullCodeMap).length != lengthCode) {
                                    focusManager.moveFocus(FocusDirection.Right)
                                }
                        }
                    },
                    textStyle = TextStyle(textAlign = TextAlign.Center),
                    modifier = Modifier
                        .clickable(enabled = !isBuffered) {

                        }
                        .onFocusChanged {
                            if (!lockBuffer)
                                if (it.isFocused) {
                                    val clipBoardManager =
                                        context.getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val bufferText =
                                        clipBoardManager.primaryClip?.getItemAt(0)?.text?.toString()
                                    if (bufferText != null)
                                        if (bufferText != getString(fullCodeMap))
                                            if (regex.matches(bufferText)) {
                                                repeat(lengthCode) {
                                                    fullCodeMap[it] = bufferText[it].toString()
                                                }
                                                focusManager.clearFocus()
                                                action(getString(fullCodeMap))
                                                isBuffered = true
                                            }
                                }
                        }
                )
            }
            Spacer(modifier = Modifier.weight(weightSpacer))
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifierOneCodeItem
                .weight(weightItem)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_cancel),
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        lockBuffer = isBuffered
                        repeat(lengthCode) {
                            fullCodeMap[it] = ""
                        }
                        isBuffered = false
                    }
            )
        }
    }
}

fun getString(map: SnapshotStateMap<Int, String>): String {

    var string = ""

    map.keys.forEach {
        string += map[it]
    }
    return string
}