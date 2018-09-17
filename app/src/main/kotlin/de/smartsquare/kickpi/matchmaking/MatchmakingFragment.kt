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

        val updateUIOnTeamUpdate = Observer<List<String>> { _ ->
            lobbyViewModel.leftTeam.value.getOrElse(0) {""}.also(firstPlayerOfLeftTeam::setText)
            lobbyViewModel.leftTeam.value.getOrElse(1) {""}.also(secondPlayerOfLeftTeam::setText)
            lobbyViewModel.rightTeam.value.getOrElse(0) {""}.also(firstPlayerOfRightTeam::setText)
            lobbyViewModel.rightTeam.value.getOrElse(1) {""}.also(secondPlayerOfRightTeam::setText)

            connectionCount.text = (lobbyViewModel.leftTeam.value.size + lobbyViewModel.rightTeam.value.size).toString()
        }

        lobbyViewModel.rightTeam.observe(this, updateUIOnTeamUpdate)
        lobbyViewModel.leftTeam.observe(this, updateUIOnTeamUpdate)
    }


}
