package de.saring.leafletmapdemo

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

/**
 * Demo application for the LeafletMap component. It displays an recorded exercise track. The user is able to
 * follow the track by moving the position slider.
 *
 * @author Stefan Saring
 */
class Main : Application() {

    override fun start(primaryStage: Stage) {
        val root: Parent = FXMLLoader.load(Main::class.java.getResource("/de/saring/leafletmapdemo/demo.fxml"))
        primaryStage.title = "LeafletMap Demo"
        primaryStage.scene = Scene(root)
        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}
