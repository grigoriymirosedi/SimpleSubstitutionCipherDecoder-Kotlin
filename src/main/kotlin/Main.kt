import java.io.File

fun main() {

    val ciphertext = File("src/cipherText.txt").readText()

    val simpleSubstitutionDescriptor = SimpleSubstitutionDescriptor()

    var decryptedText = simpleSubstitutionDescriptor.decryptUsingStochasticOptimization(ciphertext = ciphertext)

    var inputList: List<String> = emptyList()

    while(true) {
        println("Расшифрованный текст: $decryptedText")
        println("Введите замену: (a b)")
        val inputReplace = readln()
        if(inputReplace == "u") {
            println("Отмена замены")
            decryptedText = decryptedText.replace(inputList[1], inputList[0])
            continue
        }
        inputList = inputReplace.split(" ")
        decryptedText = decryptedText.replace(inputList[0], inputList[1])
    }
}


