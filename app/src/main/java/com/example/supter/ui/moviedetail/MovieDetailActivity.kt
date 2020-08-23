package com.example.supter.ui.moviedetail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.supter.MOVIE_MESSAGE
import com.example.supter.R
import com.example.supter.data.db.entity.MovieEntity
import com.example.supter.ui.ScopedActivity
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.io.ByteArrayOutputStream
import java.lang.StringBuilder

class MovieDetailActivity : ScopedActivity(), KodeinAware {

    //Kodein for dependency injections
    override val kodein by closestKodein()

    //ViewModelFactory
    private val viewModelFactory: MovieDetailViewModelFactory by instance()
    private lateinit var viewModel: MovieDetailViewModel

    private lateinit var mHeaderTitle: TextView
    private lateinit var mHeaderVoteAverage: TextView
    private lateinit var mOverview: TextView
    private lateinit var mHeaderImageView: ImageView
    private lateinit var movie: MovieEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        mHeaderImageView = findViewById(R.id.imageview_header)
        mHeaderTitle = findViewById(R.id.textview_title)
        mHeaderVoteAverage = findViewById(R.id.rating)
        mOverview = findViewById(R.id.overview_detail)

        ViewCompat.setTransitionName(imageview_header, VIEW_NAME_HEADER_IMAGE)
        ViewCompat.setTransitionName(textview_title, VIEW_NAME_HEADER_TITLE)
        ViewCompat.setTransitionName(mHeaderVoteAverage, VIEW_NAME_HEADER_VOTE_AVERAGE)
        ViewCompat.setTransitionName(mOverview, VIEW_NAME_OVERVIEW)

        //Init view model
        viewModel = ViewModelProvider(this, viewModelFactory).get(MovieDetailViewModel::class.java)

        movie = intent.getSerializableExtra(MOVIE_MESSAGE) as MovieEntity

        loadItem()

        bindUI()
    }

    private fun bindUI() = launch {
        viewModel.fetchMovie(movie.id)
        val movie = viewModel.movie.await()

        movie.observe(this@MovieDetailActivity, Observer {
            if (it == null) return@Observer

            directors_detail.text = it.production_companies[0].name

            val genresString = StringBuilder()
            for(genre in it.genres){
                genresString.append(" ${genre.name.capitalize()},")
            }

            genres_detail.text = genresString.substring(0, genresString.length-1).trim()
        })

    }


    private fun loadItem() {
        // Set the title TextView to the item's name

        if (movie.title.isEmpty()) {
            movie.title = "Empty"
        }
        if (movie.release_date == null || movie.release_date!!.isEmpty()) {
            movie.release_date = "Empty"
        }

        if (movie.overview == null || movie.overview!!.isEmpty()) {
            movie.overview = "Empty"
        }

        if (movie.title != "Empty" && movie.release_date!! != "Empty") {
            val titleDate = movie.title + " (" + movie.release_date?.replace("-", ".") + ")"
            val spannableStringBuilder = SpannableStringBuilder(titleDate)

            val gray = ForegroundColorSpan(getColor(R.color.gray))

            spannableStringBuilder.setSpan(
                gray,
                movie.title.length,
                titleDate.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            mHeaderTitle.text = spannableStringBuilder
        } else {
            mHeaderTitle.text = movie.title
        }

        // Set vote average TextView to the item's vote average
        mHeaderVoteAverage.text = movie.vote_average.toString()

        // Set overview TextView to the item's overview
        overview_detail.text = movie.overview

        // Set image ImageView to the item's image
        loadThumbnail()
    }

    private fun loadThumbnail() {

        val bm: Bitmap
        bm = if (movie.image == null) {
            val d: Drawable? = ContextCompat.getDrawable(this, R.mipmap.default_film)
            val bitmap = (d as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            bitmap
        } else {
            BitmapFactory.decodeByteArray(
                movie.image,
                0,
                movie.image!!.size
            )
        }

        imageview_header.setImageBitmap(bm)
    }

    companion object {
        // View name of the header image. Used for activity scene transitions
        const val VIEW_NAME_HEADER_IMAGE = "detail:header:image"

        // View name of the header title. Used for activity scene transitions
        const val VIEW_NAME_HEADER_TITLE = "detail:header:title"

        // View name of the vote average. Used for activity scene transitions
        const val VIEW_NAME_HEADER_VOTE_AVERAGE = "detail:header:vote_average"

        // View name of the overview. Used for activity scene transitions
        const val VIEW_NAME_OVERVIEW = "detail:overview"

    }

}
