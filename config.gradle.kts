// Add this import at the top
import java.io.File
import java.io.FileInputStream
import java.util.Properties
import org.gradle.api.GradleException

// The 'project' object is implicitly available in this script as 'this'.
// You can directly access its properties and extensions without any prefix.

// Load local.properties file if it exists from the root project directory
// Correctly access the root project's directory and then find the file.
val localPropertiesFile = File(rootDir, "local.properties")

if (localPropertiesFile.exists()) {
    val props = Properties()
    // Explicitly use FileInputStream for the 'use' block
    FileInputStream(localPropertiesFile).use { inputStream ->
        props.load(inputStream)
    }

    val auth0Properties = listOf(
        "AUTH0_DOMAIN",
        "AUTH0_CLIENT_ID"
    )

    // Access the project's extra properties extension
    val extraProps = extensions.extraProperties

    // Iterate through desired keys and expose them to Gradle
    auth0Properties.forEach { key ->
        val value = props.getProperty(key)
        if (value != null) {
            val sanitizedValue = value.trim().removeSurrounding("\"")
            extraProps.set(key, sanitizedValue)
            println("Injected property: $key") // optional debug
        } else {
            throw GradleException("Missing required property '$key' in local.properties.")
        }
    }
} else {
    throw GradleException(
        "local.properties file not found. Please create it and add AUTH0_DOMAIN and AUTH0_CLIENT_ID."
    )
}
