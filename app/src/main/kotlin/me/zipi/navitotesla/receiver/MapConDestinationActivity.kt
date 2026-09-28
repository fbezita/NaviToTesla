package me.zipi.navitotesla.receiver

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import me.zipi.navitotesla.model.Poi
import me.zipi.navitotesla.service.NaviToTeslaService
import me.zipi.navitotesla.util.AnalysisUtil

/** MapCon의 전경 호출을 받아 Tesla 공유 화면을 열 수 있는 짧은 중계 Activity. */
class MapConDestinationActivity : ComponentActivity() {
    private var handled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setDimAmount(0f)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onResume() {
        super.onResume()
        if (handled) return
        handled = true

        val destination = MapConDestinationReceiver().parse(intent)
        if (destination == null) {
            AnalysisUtil.warn("invalid MapCon destination activity intent")
            finish()
            return
        }

        lifecycleScope.launch {
            AnalysisUtil.log("received MapCon destination in foreground: ${destination.name}")
            NaviToTeslaService(applicationContext).shareExternalDestination(
                Poi(
                    poiName = destination.name,
                    roadAddress = destination.coordinates,
                    address = destination.address,
                    longitude = destination.longitude.toString(),
                    latitude = destination.latitude.toString(),
                    packageName = MapConDestinationReceiver.MAPCON_PACKAGE,
                ),
            )
            finish()
        }
    }
}
