package me.zipi.navitotesla.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zipi.navitotesla.model.Poi
import me.zipi.navitotesla.service.NaviToTeslaService
import java.util.Locale

/** MapCon의 공개 목적지 출력 API에서 정확한 목적지를 받는다. */
class MapConDestinationReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val destination = parse(intent) ?: return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                NaviToTeslaService(context.applicationContext).shareExternalDestination(
                    Poi(
                        poiName = destination.name,
                        roadAddress = destination.coordinates,
                        address = destination.address,
                        longitude = destination.longitude.toString(),
                        latitude = destination.latitude.toString(),
                        packageName = MAPCON_PACKAGE,
                    ),
                )
            } finally {
                pendingResult.finish()
            }
        }
    }

    internal data class Destination(
        val name: String,
        val address: String,
        val latitude: Double,
        val longitude: Double,
    ) {
        val coordinates: String
            get() = String.format(Locale.US, "%.7f,%.7f", latitude, longitude)
    }

    internal fun parse(intent: Intent): Destination? {
        if (intent.action != ACTION_DESTINATION_STARTED) return null
        val name = intent.getStringExtra(EXTRA_NAME)?.trim().orEmpty()
        val address = intent.getStringExtra(EXTRA_ADDRESS)?.trim().orEmpty()
        val latitude = intent.getDoubleExtra(EXTRA_LATITUDE, Double.NaN)
        val longitude = intent.getDoubleExtra(EXTRA_LONGITUDE, Double.NaN)
        if (name.isEmpty() || !latitude.isFinite() || !longitude.isFinite()) return null
        if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) return null
        return Destination(name, address, latitude, longitude)
    }

    companion object {
        const val MAPCON_PACKAGE = "ai.rhinos.mapcon"
        const val ACTION_DESTINATION_STARTED =
            "ai.rhinos.mapcon.action.DESTINATION_STARTED"
        const val EXTRA_NAME = "ai.rhinos.mapcon.extra.DESTINATION_NAME"
        const val EXTRA_ADDRESS = "ai.rhinos.mapcon.extra.DESTINATION_ADDRESS"
        const val EXTRA_LATITUDE = "ai.rhinos.mapcon.extra.DESTINATION_LATITUDE"
        const val EXTRA_LONGITUDE = "ai.rhinos.mapcon.extra.DESTINATION_LONGITUDE"
    }
}
