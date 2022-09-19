package com.foggyskies.petapp.routs

import com.foggyskies.petapp.BuildConfig

object Routes {

    object SERVER {

        const val BASE_IP =
            BuildConfig.BaseIP + ':' + BuildConfig.BasePort

        object WEBSOCKETCOMMANDS {

            //            companion object {
            const val BASE_URL = "ws://$BASE_IP"
            const val COMMAND_SYMBOL = "|"

            const val CHATS = "getChats"
            const val FRIENDS = "getFriends"
            const val INTERNAL_NOTIFICATIONS = "getInternalNotification"
            const val PAGES_PROFILE = "getPagesProfile"

            const val MAIN_SOCKET = "/mainSocket/"
            const val CHAT_SOCKET = "/subscribes/"

            const val CHAT_SESSION = "/subscribes/chatSessions"
        }

        object REQUESTS {

            object Content {
                const val ADD_COMMENT_TO_POST = "/content/addCommentToPost"
                const val ADD_POST_IMAGE = "/content/addPostImage"
                const val CONTENT_PAGE = "/content/getContentPreview"
                const val POSTS = "/content/getPosts"
                const val COMMENTS = "/content/getComments"
                const val LIKED_USERS = "/content/getLikedUsers"
                const val ONE_POST_INFO = "/content/getInfoAboutOnePost"
            }

            object Auth {
                const val GENERATE_CODE = "/generateCode"
                const val REGISTRATION = "/registration"
                const val CHECK_TOKEN = "/checkToken"
                const val AUTH = "/auth"
            }

            object ChatRoute {
                const val CREATE_CHAT = "/createChat"
                const val CREATE_CHAT_SESSION = "/subscribes/createChatSession?idChat="
                const val DELETE_MESSAGE = "/subscribes/deleteMessage"
                const val EDIT_MESSAGE = "/subscribes/editMessage"
                const val MESSAGE_WITH_CONTENT = "/subscribes/sendMessageWithContent"
                const val GET_MESSAGES = "/subscribes/getMessages"
                const val GET_NEW_MESSAGES = "/subscribes/getNewMessages"
            }

            object UserRoute {
                const val ADD_FRIEND = "/addFriend"
                const val MUTE_CHAT = "/muteChat"
                const val GET_CHATS = "/getChats"
                const val GET_FRIENDS = "/getFriends"
                const val AVATAR = "/avatar"
                const val CHANGE_AVATAR = "/changeAvatar"
                const val PAGES_OTHER_PROFILE = "/getPagesProfileByIdUser"
                const val PAGES_MY_PROFILE = "/getPagesProfile"
                const val CHANGE_AVATAR_PROFILE = "/changeAvatarProfile"
                const val REQUESTS_FRIENDS = "/getRequestsFriends"
                const val NEW_MESSAGES = "/getNewMessages"
                const val LOG_OUT = "/logOut"
                const val SEARCH_USER = "/searchUser"
                const val CREATE_PAGE_PROFILE = "/createPageProfile"
                const val CREATE_MAIN_SOCKET = "/createMainSocket"
                const val DELETE_PAGE_PROFILE = "/deletePageProfile"
            }

            const val BASE_URL = "http://$BASE_IP"

            const val CLOUD_ALL_TREE = "/cloud/allTree"

            //            const val FILE_LOAD = "/subscribes/fileLoad"
            const val FILE_LOAD = "/fileLoad"

        }
    }

    object FILE {

        const val ANDROID_DIR = "/storage/emulated/0"

        const val DOWNLOAD_DIR = "/Download"
        const val MAIN_APP_DIR = "$ANDROID_DIR/RusLan"
        const val IMAGES = "/Images"
    }
}