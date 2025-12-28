import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType

interface MappingExtension {
    val mcVersion: Property<String>
}

class MappingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Get existing extension or create new one
        val extension = project.extensions.findByType<MappingExtension>()
            ?: project.extensions.create("mapping", MappingExtension::class.java)

        project.tasks.withType<SpigotRemapTask>().configureEach {
            version.set(extension.mcVersion)
        }

        // Use afterEvaluate only for dependency resolution which requires the mcVersion value
        project.afterEvaluate {
            val mcVersion = extension.mcVersion.get()
            project.dependencies.add("compileOnly", "org.spigotmc:spigot:$mcVersion-R0.1-SNAPSHOT:remapped-mojang@jar") {
                isTransitive = false
            }
            project.dependencies.add("compileOnly", "org.spigotmc:spigot-api:$mcVersion-R0.1-SNAPSHOT") {
                isTransitive = false
            }
        }
    }
}
