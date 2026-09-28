package me.zipi.navitotesla.receiver

import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class MapConDestinationReceiverTest {
    private val receiver = MapConDestinationReceiver()

    @Test
    fun `MapCon 목적지 좌표를 그대로 읽는다`() {
        val intent =
            Intent(MapConDestinationReceiver.ACTION_DESTINATION_STARTED)
                .putExtra(MapConDestinationReceiver.EXTRA_NAME, "서울역")
                .putExtra(MapConDestinationReceiver.EXTRA_ADDRESS, "서울 중구 한강대로 405")
                .putExtra(MapConDestinationReceiver.EXTRA_LATITUDE, 37.554722)
                .putExtra(MapConDestinationReceiver.EXTRA_LONGITUDE, 126.970833)

        val destination = receiver.parse(intent)

        assertEquals("서울역", destination?.name)
        assertEquals("서울 중구 한강대로 405", destination?.address)
        assertEquals("37.5547220,126.9708330", destination?.coordinates)
    }

    @Test
    fun `좌표가 없으면 목적지를 무시한다`() {
        val intent =
            Intent(MapConDestinationReceiver.ACTION_DESTINATION_STARTED)
                .putExtra(MapConDestinationReceiver.EXTRA_NAME, "서울역")

        assertNull(receiver.parse(intent))
    }

    @Test
    fun `다른 action은 무시한다`() {
        assertNull(receiver.parse(Intent("example.action.OTHER")))
    }
}
