package com.example.ksp

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate
import kotlin.reflect.KClass

internal class HackedProcessor(
    private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {

    companion object {
        const val GENERATED_PACKAGE = "generated.file"
        const val GENERATED_FILE_NAME = "GeneratedFile"
    }
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val hackedFunctions: Sequence<KSFunctionDeclaration> =
            resolver.findAnnotations(Hacked::class)

        if(!hackedFunctions.iterator().hasNext()) return emptyList()

        val sourceFiles = hackedFunctions.mapNotNull { it.containingFile }

        val fileText = buildString {
            append("package $GENERATED_PACKAGE")
            newLine(2)
            append("import android.util.Log")
            newLine(2)

            append("fun printHackFunction() = \"\"\"")
            newLine()
            hackedFunctions
                .mapNotNull {
                    it.qualifiedName?.asString()
                }.forEach {
                    append(it)
                    newLine()
                }
            append("\"\"\"")
            newLine()
        }

        environment.logger.info("DONE!")

        createFileWithText(sourceFiles, fileText)
        return (hackedFunctions).filterNot { it.validate() }.toList()
    }

    private fun Resolver.findAnnotations(
        kClass: KClass<*>,
    ) = getSymbolsWithAnnotation(
        kClass.qualifiedName.toString())
        .filterIsInstance<KSFunctionDeclaration>()

    private fun createFileWithText(
        sourceFiles: Sequence<KSFile>,
        fileText: String,
    ) {
        val file = environment.codeGenerator.createNewFile(
            Dependencies(
                false,
                *sourceFiles.toList().toTypedArray(),
            ),
            GENERATED_PACKAGE,
            GENERATED_FILE_NAME
        )

        file.write(fileText.toByteArray())
    }

    private fun StringBuilder.newLine(count: Int = 1) {
        repeat(count){
            append("\n")
        }
    }
}
