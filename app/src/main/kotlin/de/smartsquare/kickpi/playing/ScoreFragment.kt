package de.smartsquare.kickpi.playing

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

class ScoreFragment : Fragment() {

    private val lobbyViewModel by sharedViewModel<LobbyViewModel>()

    private val leftScore by bindView<TextView>(R.id.scoreLeft)
    private val rightScore by bindView<TextView>(R.id.scoreRight)

    private val firstPlayerOfLeftTeam by bindView<TextView>(R.id.firstPlayerOfLeftTeam)
    private val secondPlayerOfLeftTeam by bindView<TextView>(R.id.secondPlayerOfLeftTeam)
    private val firstPlayerOfRightTeam by bindView<TextView>(R.id.firstPlayerOfRightTeam)
    private val secondPlayerOfRightTeam by bindView<TextView>(R.id.secondPlayerOfRightTeam)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_score, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lobbyViewModel.scoreLeft.observe(this, Observer { it?.let(Int::toString).also(leftScore::setText) })
        lobbyViewModel.scoreRight.observe(this, Observer { it?.let(Int::toString).also(rightScore::setText) })

        lobbyViewModel.leftTeam.value.getOrElse(0) { "" }.also(firstPlayerOfLeftTeam::setText)
        lobbyViewModel.leftTeam.value.getOrElse(1) { "" }.also(secondPlayerOfLeftTeam::setText)
        lobbyViewModel.rightTeam.value.getOrElse(0) { "" }.also(firstPlayerOfRightTeam::setText)
        lobbyViewModel.rightTeam.value.getOrElse(1) { "" }.also(secondPlayerOfRightTeam::setText)
    }
}