package jp.techacademy.takumi.tomizawa.apiapp

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class FavoriteShop(
    id: String,
    imageUrl: String,
    name: String,
    address: String,
    url: String,
    favoriteCheck: Boolean
) : RealmObject {
    @PrimaryKey
    var id: String = ""
    var imageUrl: String = ""
    var name: String = ""
    var address: String = ""
    var url: String = ""
    var favoriteCheck: Boolean = true

    // 初期化処理
    init {
        this.id = id
        this.imageUrl = imageUrl
        this.name = name
        this.address = address
        this.url = url
        this.favoriteCheck = favoriteCheck
    }

    // realm内部呼び出し用にコンストラクタを用意
    constructor() : this("", "", "", "", "", true)

    companion object {
        /**
         * お気に入りのShopを全件取得
         */
        fun findAll(): List<FavoriteShop> {
            // Realmデータベースとの接続を開く
            val config = RealmConfiguration.create(schema = setOf(FavoriteShop::class))
            val realm = Realm.open(config)
            val isFavorite = true

            // RealmデータベースからdeleteFragがfalseのお気に入り情報を取得
            // mapでディープコピーしてresultに代入する
            val result = realm.query<FavoriteShop>("favoriteCheck==$isFavorite").find()
                .map {
                    FavoriteShop(
                        it.id,
                        it.imageUrl,
                        it.name,
                        it.address,
                        it.url,
                        it.favoriteCheck
                    )
                }

            // Realmデータベースとの接続を閉じる
            realm.close()

            return result
        }

        /*fun findDeleteStore(): List<FavoriteShop>{
            // Realmデータベースとの接続を開く
            val config = RealmConfiguration.create(schema = setOf(FavoriteShop::class))
            val realm = Realm.open(config)
            val deleteCheck = true

            // Realmデータベースからお気に入り情報を取得
            // deleteFragがtrueのデータのみを取得する
            val deleteStore = realm.query<FavoriteShop>("deleteFrag==$deleteCheck").find()
                .map { FavoriteShop(it.id, it.imageUrl, it.name, it.address, it.url, it.deleteFrag) }

            // Realmデータベースとの接続を閉じる
            realm.close()

            return deleteStore
        }*/

        /**
         * お気に入りされているShopをidで検索して返す
         * お気に入りに登録されていなければnullで返す
         */
        fun findBy(id: String): FavoriteShop? {
            // Realmデータベースとの接続を開く
            val config = RealmConfiguration.create(schema = setOf(FavoriteShop::class))
            val realm = Realm.open(config)

            val result = realm.query<FavoriteShop>("id=='$id'").first().find()

            // Realmデータベースとの接続を閉じる
            realm.close()

            return result
        }

        /**
         * お気に入り追加
         */
        fun insert(favoriteShop: FavoriteShop) {
            // Realmデータベースとの接続を開く
            val config = RealmConfiguration.create(schema = setOf(FavoriteShop::class))
            val realm = Realm.open(config)

            // 登録処理
            realm.writeBlocking {
                copyToRealm(favoriteShop)
            }

            // Realmデータベースとの接続を閉じる
            realm.close()
        }

        fun update(id: String) {
            // Realmデータベースとの接続を開く
            val config = RealmConfiguration.create(schema = setOf(FavoriteShop::class))
            val realm = Realm.open(config)

            // 削除処理
            realm.writeBlocking {
                val favoriteShop = query<FavoriteShop>("id == '$id'").first().find()
                if (favoriteShop != null) {
                    favoriteShop.favoriteCheck = true
                }
            }
        }

        /**
         * idでお気に入りから削除する
         */
        fun delete(id: String) {
            // Realmデータベースとの接続を開く
            val config = RealmConfiguration.create(schema = setOf(FavoriteShop::class))
            val realm = Realm.open(config)

            // 削除処理
            realm.writeBlocking {
                val favoriteShop = query<FavoriteShop>("id == '$id'").first().find()
                if (favoriteShop != null) {
                    favoriteShop.favoriteCheck = false
                }
            }

            // Realmデータベースとの接続を閉じる
            realm.close()
        }
    }
}