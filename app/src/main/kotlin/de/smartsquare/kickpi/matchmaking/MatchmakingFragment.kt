package de.smartsquare.kickpi.matchmaking

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.smartsquare.kickpi.R
import de.smartsquare.kickpi.domain.LobbyViewModel
import kotterknife.bindView
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class MatchmakingFragment : Fragment() {


    private val lobbyViewModel by sharedViewModel<LobbyViewModel>()

    private val firstPlayerOfLeftTeam by bindView<TextView>(R.id.firstPlayerLeft)
    private val secondPlayerOfLeftTeam by bindView<TextView>(R.id.secondPlayerLeft)
    private val firstPlayerOfRightTeam by bindView<TextView>(R.id.firstPlayerRight)
    private val secondPlayerOfRightTeam by bindView<TextView>(R.id.secondPlayerRight)
    private val connectionCount by bindView<TextView>(R.id.connectionCount)

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_matchmaking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val onPlayerJoined = Observer<List<String>> {
            if (lobbyViewModel.leftTeam.value.isNotEmpty()) {
                firstPlayerOfLeftTeam.text = lobbyViewModel.leftTeam.value[0]
            }
            if (lobbyViewModel.leftTeam.value.size > 1) {
                secondPlayerOfLeftTeam.text = lobbyViewModel.leftTeam.value[1]
            }
            if (lobbyViewModel.rightTeam.value.isNotEmpty()) {
                firstPlayerOfRightTeam.text = lobbyViewModel.rightTeam.value[0]
            }
            if (lobbyViewModel.rightTeam.value.size > 1) {
                secondPlayerOfRightTeam.text = lobbyViewModel.rightTeam.value[1]
            }

            connectionCount.text = (lobbyViewModel.leftTeam.value.size + lobbyViewModel.rightTeam.value.size).toString()
        }

        lobbyViewModel.rightTeam.observe(this, onPlayerJoined)
        lobbyViewModel.leftTeam.observe(this, onPlayerJoined)
    }


}
