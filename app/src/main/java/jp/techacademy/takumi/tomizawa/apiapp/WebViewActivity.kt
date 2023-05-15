package jp.techacademy.takumi.tomizawa.apiapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import com.squareup.picasso.Picasso
import jp.techacademy.takumi.tomizawa.apiapp.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.loadUrl(intent.getStringExtra(KEY_URL).toString())
        binding.couponNameTextView.text = intent.getStringExtra(NAME)
        Picasso.get().load(intent.getStringExtra(IMAGE)).into(binding.couponImageView)

        binding.couponFavoriteImageView.apply {
            // お気に入り状態を取得
            val isFavorite = FavoriteShop.findBy(intent.getStringExtra(ID)!!) != null

            val couponUrls = CouponUrls(intent.getStringExtra(KEY_URL)!!, intent.getStringExtra(KEY_URL)!!)

            val shop = Shop(
                couponUrls,
                intent.getStringExtra(ADDRESS)!!,
                intent.getStringExtra(ID)!!,
                intent.getStringExtra(IMAGE)!!,
                intent.getStringExtra(NAME)!!
            )

            // 白抜きの星を設定
            setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)

            // 星をタップした時の処理
            setOnClickListener {
                val isFavorite = FavoriteShop.findBy(intent.getStringExtra(ID)!!) != null

                if (isFavorite) {
                    onDeleteFavorite(ID)
                } else {
                    onAddFavorite(shop)
                }
            }
        }
    }

    private fun onDeleteFavorite(id: String) {
        showConfirmDeleteFavoriteDialog(id)
    }

    private fun onAddFavorite(shop: Shop) {
        var handler = Handler(Looper.getMainLooper())
        FavoriteShop.insert(FavoriteShop().apply {
            id = shop.id
            name = shop.name
            imageUrl = shop.logoImage
            address = shop.address
            url = shop.couponUrls.sp.ifEmpty { shop.couponUrls.pc }
        })
        handler.post {
            binding.couponFavoriteImageView.setImageResource(R.drawable.ic_star)
        }
    }

    private fun showConfirmDeleteFavoriteDialog(id: String) {
        var handler = Handler(Looper.getMainLooper())

        AlertDialog.Builder(this)
            .setTitle(R.string.delete_favorite_dialog_title)
            .setMessage(R.string.delete_favorite_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                deleteFavorite()
                handler.post {
                    binding.couponFavoriteImageView.setImageResource(R.drawable.ic_star_border)
                }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .create()
            .show()
    }

    private fun deleteFavorite() {
        FavoriteShop.delete(intent.getStringExtra(ID)!!)
    }

    companion object {
        private const val KEY_URL = "key_url"
        private const val ID = "id"
        private const val ADDRESS = "address"
        private const val NAME = "name"
        private const val IMAGE = "image"

        fun start(activity: Activity, shop: Shop) {
            activity.startActivity(
                Intent(activity, WebViewActivity::class.java).putExtra(
                    KEY_URL,
                    shop.couponUrls.sp.ifEmpty { shop.couponUrls.pc }
                ).putExtra(
                    NAME, shop.name
                ).putExtra(
                    IMAGE, shop.logoImage
                ).putExtra(
                    ADDRESS, shop.address
                ).putExtra(
                    ID, shop.id
                )
            )
        }
    }
}