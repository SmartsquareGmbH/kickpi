package de.smartsquare.kickpi

import android.app.Activity
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.things.pio.PeripheralManager
import com.squareup.moshi.Moshi
import de.smartsquare.kickpi.BuildConfig.KICKWAY_URL
import de.smartsquare.kickpi.domain.LobbyViewModel
import de.smartsquare.kickpi.idle.ConnectUseCase
import de.smartsquare.kickpi.idle.CreateGameUseCase
import de.smartsquare.kickpi.matchmaking.JoinLobbyUseCase
import de.smartsquare.kickpi.matchmaking.LeaveLobbyUseCase
import de.smartsquare.kickpi.matchmaking.StartGameUseCase
import de.smartsquare.kickpi.navbar.KickwayStatisticsRepository
import de.smartsquare.kickpi.navbar.StatisticsService
import de.smartsquare.kickpi.navbar.TopThreeViewModel
import de.smartsquare.kickpi.playing.GameRepository
import de.smartsquare.kickpi.playing.KickchainGameRepository
import de.smartsquare.kickprotocol.Kickprotocol
import org.koin.android.viewmodel.ext.koin.viewModel
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

    single { get<Retrofit>().create(KickwayStatisticsRepository::class.java) }

    factory { StatisticsService(get()) }

    factory { GameRepository(get()) }
}

private val hardware = module {
    single { PeripheralManager.getInstance() }
}

private val kickprotocol = module {
    factory { (activity: Activity) -> Nearby.getConnectionsClient(activity) }
    scope("activity") { parametersList -> Kickprotocol(get<ConnectionsClient>(parameters = { parametersList }), get()) }
}

private val domain = module {
    single { Endpoints() }
}

private val viewModels = module {
    viewModel { TopThreeViewModel(get()) }
    viewModel { LobbyViewModel() }
}

val modules = listOf(network, hardware, kickprotocol, domain, viewModels)