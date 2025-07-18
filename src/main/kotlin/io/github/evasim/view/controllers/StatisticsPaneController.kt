package io.github.evasim.view.controllers

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.chart.AreaChart
import javafx.scene.chart.XYChart
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import java.net.URL
import java.util.ResourceBundle

@Suppress("detekt:VarCouldBeVal")
internal class StatisticsPaneController : Initializable {

    @FXML private lateinit var statisticsPanel: VBox

    @FXML private lateinit var toggleButton: Button

    @FXML private lateinit var areaChart: AreaChart<Number, Number>

    @FXML private lateinit var hawkyBlobsLabel: Label

    @FXML private lateinit var doveBlobsLabel: Label

    private val doveSeries = XYChart.Series<Number, Number>().apply { name = "Dove Blobs" }

    private val hawkSeries = XYChart.Series<Number, Number>().apply { name = "Hawk Blobs" }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        ToggleController(toggleButton, statisticsPanel)
        areaChart.data.addAll(hawkSeries, doveSeries)
    }

    internal fun updateData(roundNumber: Int, doveBlobs: Int, hawkyBlobs: Int) = Platform.runLater {
        hawkyBlobsLabel.text = hawkyBlobs.toString()
        doveBlobsLabel.text = doveBlobs.toString()
        // Dove blobs: go from 0 to doveBlobs
        doveSeries.data.add(XYChart.Data(roundNumber, doveBlobs))
        // Hawk blobs: go from doveBlobs to doveBlobs + hawkyBlobs (stacked on top)
        val stackedY = doveBlobs + hawkyBlobs
        hawkSeries.data.add(XYChart.Data(roundNumber, stackedY))
    }

    internal fun clearData() {
        doveSeries.data.clear()
        hawkSeries.data.clear()
    }
}
