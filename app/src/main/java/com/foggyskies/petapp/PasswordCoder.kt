package com.foggyskies.petapp

object PasswordCoder {

    private fun getCodes(forthElement: Int): Int {
        var newCodeChar = 0
        if (forthElement.toString().length != 3)
            forthElement.toString().forEach {
                newCodeChar += it.code
            }
        else {
            newCodeChar += forthElement.toString()[0].code
            newCodeChar += forthElement.toString()[2].code
        }
        var newNewCodeElem = 0
        newCodeChar.toString().forEach {
            newNewCodeElem += it.toString().toInt()
        }
        return newNewCodeElem
    }

    fun encodeStringFS(s: String): String {

        var encodedString = ""

        s.forEach { char ->
            var codeString = ""
            repeat(12) {
                val oneCharCode =
                    if (it == 1) {
                        var charCode = 0
                        do {
                            charCode = kotlin.random.Random.nextInt(from = 32, until = 126)
                        } while (charCode in 48..57 || charCode in 65..72)
                        charCode
                    } else
                        kotlin.random.Random.nextInt(from = 32, until = 126)
                val oneChar = Char(oneCharCode)
                codeString += oneChar
            }
            val forthElement = codeString[4].code
            val newNewCodeElem = getCodes(forthElement)
            val newChar = {
                val sum = char.code + newNewCodeElem
                if (sum > 126) {
                    val countOverCup =
                        when (126 - char.code) {
                            in 0..9 ->
                                (126 - char.code).toString()
                            10 ->
                                Char(65)
                            11 ->
                                Char(66)
                            12 ->
                                Char(67)
                            13 ->
                                Char(68)
                            14 ->
                                Char(69)
                            15 ->
                                Char(70)
                            16 ->
                                Char(71)
                            17 ->
                                Char(72)
                            else ->
                                Char(99)
                        }
                    codeString = codeString.replaceRange(1..1, countOverCup.toString())
                    Char(sum - 94)
                } else {
                    Char(sum)
                }
            }
            val encodedChar = newChar()
            encodedString += codeString + encodedChar
        }
        return encodedString
    }

    fun decodeStringFS(encodedString: String): String {
        val encodedSectors = encodedString.toList().chunked(13)
        val decodedChars = encodedSectors.mapIndexed { index, list ->
            val code = getCodes(list[4].code)
            when (list[1].code) {
                in 48..57 ->
                    Char((126 - list[1].toString().toInt()))
                65 ->
                    Char(126 - 10)
                66 ->
                    Char(126 - 11)
                67 ->
                    Char(126 - 12)
                68 ->
                    Char(126 - 13)
                69 ->
                    Char(126 - 14)
                70 ->
                    Char(126 - 15)
                71 ->
                    Char(126 - 16)
                72 ->
                    Char(126 - 17)
                else ->
                    Char(list.last().code - code)
            }
        }
        var decodedString = ""
        decodedChars.forEach {
            decodedString += it
        }
        return decodedString
    }
}