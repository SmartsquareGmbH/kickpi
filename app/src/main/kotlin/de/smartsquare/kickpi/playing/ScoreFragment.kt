package de.smartsquare.kickpi.playing

import android.arch.lifecycle.Observer
import android.graphics.Color
import android.graphics.Color.RED
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.smartsquare.kickpi.R
import de.smartsquare.kickpi.domain.LobbyViewModel
import kotlinx.android.synthetic.main.fragment_score.viewKonfetti
import kotterknife.bindView
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape.RECT
import nl.dionsegijn.konfetti.models.Shape.CIRCLE
import nl.dionsegijn.konfetti.models.Size
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ScoreFragment : Fragment() {

    private val lobbyViewModel by sharedViewModel<LobbyViewModel>()

    private val leftScore by bindView<TextView>(R.id.scoreLeft)
    private val rightScore by bindView<TextView>(R.id.scoreRight)
    private val confettiContainer by bindView<KonfettiView>(R.id.viewKonfetti)

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_score, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lobbyViewModel.scoreLeft.observe(this, Observer { it?.let(Int::toString).also(leftScore::setText) })
        lobbyViewModel.scoreRight.observe(this, Observer { it?.let(Int::toString).also(rightScore::setText) })
    }
}