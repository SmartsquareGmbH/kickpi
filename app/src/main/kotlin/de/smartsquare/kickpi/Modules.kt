package de.smartsquare.kickpi

import android.app.Activity
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.things.pio.PeripheralManager
import com.squareup.moshi.Moshi
import de.smartsquare.kickpi.BuildConfig.KICKWAY_URL
import de.smartsquare.kickpi.gameserver.Lobby
import de.smartsquare.kickprotocol.Kickprotocol
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private val network = module {
    single { Moshi.Builder().build() }

    single {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(KICKWAY_URL)
            .build()
    }

    single { get<Retrofit>().create(KickchainGameRepository::class.java) }

    single { get<Retrofit>().create(KickwayAuthorizationRepository::class.java) }
}

private val hardware = module {
    single { PeripheralManager.getInstance() }
}

private val kickprotocol = module {
    factory { (activity: Activity) -> Nearby.getConnectionsClient(activity) }
    factory { parametersList -> Kickprotocol(get<ConnectionsClient>(parameters = { parametersList }), get()) }
}

private val domain = module {
    single { KickPiLobby() }
}

val modules = listOf(network, hardware, kickprotocol, domain)