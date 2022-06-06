package com.foggyskies.petapp.routs

object Routes {

    object SERVER {
        //        companion object {
//            const val BASE_IP = "192.168.0.88:2525"
//        94.41.84.183:2526
        const val BASE_IP = "192.168.0.88:2525"

//        }

        object WEBSOCKETCOMMANDS {

            //            companion object {
            const val BASE_URL = "ws://$BASE_IP"
            const val COMMAND_SYMBOL = "|"

            const val CHATS = "getChats"
            const val FRIENDS = "getFriends"
            const val REQUESTS_FRIENDS = "getRequestsFriends"
            const val INTERNAL_NOTIFICATIONS = "getInternalNotification"
            const val PAGES_PROFILE = "getPagesProfile"

            const val MAIN_SOCKET = "/mainSocket/"
            const val CHAT_SOCKET = "/subscribes/"
//            }
        }

        object REQUESTS {

            //            companion object {
            const val BASE_URL = "http://$BASE_IP"
            const val AUTH = "/auth"
            const val REGISTRATION = "/registration"
            const val POSTS = "/content/getPosts"
            const val CREATE_MAIN_SOCKET = "/createMainSocket"
            const val CONTENT_PAGE = "/content/getContentPreview"
            const val ONE_POST_INFO = "/content/getInfoAboutOnePost"
            const val CREATE_PAGE_PROFILE = "/createPageProfile"
            const val ADD_POST_IMAGE = "/content/addPostImage"
            const val AVATAR = "/avatar"
            const val CHANGE_AVATAR = "/changeAvatar"
            const val PAGES_OTHER_PROFILE = "/getPagesProfileByIdUser"
            const val CREATE_CHAT_SESSION = "/subscribes/createChatSession?idChat="
            const val ADD_IMAGE_TO_POST = "/subscribes/addImageToMessage"
            const val MUTE_CHAT = "/muteChat"
//            }
        }
    }

    object FILE {

        //        companion object{
        const val ANDROID_DIR = "/storage/emulated/0"

        const val DOWNLOAD_DIR = "/Download"
        const val MAIN_APP_DIR = "$ANDROID_DIR/RusLan"
        const val IMAGES = "/Images"
//        }
    }
}