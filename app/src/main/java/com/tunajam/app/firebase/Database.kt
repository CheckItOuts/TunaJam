
package com.tunajam.app.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.json.JSONObject.NULL
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

const val TAG = "Database class file"
class Database {
    // Variables de la class
    private val db = Firebase.firestore

    // Fonctions add

    /**
     * Ajouter un utilisateur à la collection users
     *
     * @param pseudo Pseudo de l'utilisateur à ajouter
     * @param photo URL de la photo de l'utilisateur
     *
     * @return None
     */
    fun addUser(pseudo : String, photo : String): Int {
        val user = hashMapOf(
            "pseudo" to pseudo,
            "photo" to photo
        )

        db.collection("users").document(pseudo)
            .set(user)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e->Log.d(TAG, "Error writing document", e) }

        return 1
    }

    /**
     * Ajouter un ami à un utilisateur
     *
     * @param pseudo Pseudo de l'utilisateur
     * @param friendPseudo Pseudo de l'ami à ajouter
     *
     * @return None
     */
    fun addFriend(pseudo : String, friendPseudo : String){
        val friend = hashMapOf(
            "friendPseudo" to friendPseudo
        )

        db.collection("users").document(pseudo).collection("friends").document(friendPseudo)
            .set(friend)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e->Log.d(TAG, "Error writing document", e) }
    }

    /**
     * Ajouter une musique à un utilisateur
     *
     * @param pseudo Pseudo de l'utilisateur
     * @param idMusic ID de la musique à ajouter
     *
     * @return None
     */
    fun addMusic(pseudo : String, idMusic : String, nameMusic : String, artistMusic : String){
        val music = hashMapOf(
            "id" to idMusic,
            "name" to nameMusic,
            "artist" to artistMusic,
            "time" to Timestamp.now()
        )

        db.collection("users").document(pseudo).collection("musics").document(idMusic)
            .set(music)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e->Log.d(TAG, "Error writing document", e) }
    }


    // Fonctions get

    /**
     * Récupérer les données d'un utilisateur
     *
     * @param pseudo Pseudo de l'utilisateur
     * @param callback Fonction de callback
     *
     * @return Données de l'utilisateur
     */
    fun getUser(pseudo: String, callback: (Map<String, Any>?) -> Unit) {
        val user = db.collection("users").document(pseudo)

        user.get()
            .addOnSuccessListener { document ->
                if (document != NULL){
                    Log.d(TAG, "User : DocumentSnapshot data : ${document.data}")
                    callback(document.data)
                }
                else {
                    Log.d(TAG, "No such document")
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "get failed with ", e)
                callback(null)
            }
    }


    /**
     * Récupérer toutes les musiques d'un utilisateur
     *
     * @param pseudo Pseudo de l'utilisateur
     *
     * @return Liste des musiques de l'utilisateur
     */
    fun getFriends(pseudo: String, callback: (List<Map<String, Any>>) -> Unit) {
        db.collection("users").document(pseudo).collection("friends")
            .get()
            .addOnSuccessListener { documents ->
                val friendsList = mutableListOf<Map<String, Any>>()
                for (document in documents) {
                    friendsList.add(document.data)
                }
                callback(friendsList)
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error getting documents: ", e)
                callback(emptyList()) // Retourner une liste vide en cas d'échec
            }
    }

    /**
     * Récupérer toutes données de tous les utilisateurs
     *
     * @return Liste de tous les utilisateurs
     */
    fun getUsers(callback: (List<Map<String, Any>>) -> Unit) {
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                val friendsList = mutableListOf<Map<String, Any>>()
                for (document in documents) {
                    friendsList.add(document.data)
                }
                callback(friendsList)
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error getting documents: ", e)
                callback(emptyList()) // Retourner une liste vide en cas d'échec
            }
    }

    /**
     * Récupérer la dernière musique d'un utilisateur
     *
     * @param pseudo Pseudo de l'utilisateur
     *
     * @return Dernière musique de l'utilisateur
     */
    fun getLastMusic(pseudo: String, callback: (Map<String, Any>?) -> Unit) {
        db.collection("users").document(pseudo).collection("musics")
            .orderBy("time", Query.Direction.DESCENDING) // Trie par ordre décroissant de la date ajoutée
            .limit(1) // Limite les résultats à 1 pour obtenir uniquement la dernière musique ajoutée
            .get()
            .addOnSuccessListener { documents ->
                var lastMusic: Map<String, Any>? = null
                for (document in documents) {
                    lastMusic = document.data
                }
                callback(lastMusic)
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error getting documents: ", e)
                callback(null) // Retourner null en cas d'échec
            }
    }

    fun getFriendPhotoByUserCollection(friendPseudo: String, onComplete: (String?) -> Unit) {
        db.collection("users").document(friendPseudo)
            .get()
            .addOnSuccessListener { document ->
                val friendData = document.data
                val friendPhotoUrl = friendData?.get("photo") as? String
                onComplete(friendPhotoUrl)
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Erreur lors de la récupération de la photo de l'ami : $e")
                onComplete(null)
            }
    }




    fun deleteFriend(userPseudo: String, friendPseudo: String){
        db.collection("users").document(userPseudo).collection("friends").document(friendPseudo)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "$friendPseudo Supprimé de la liste d'amis de $userPseudo") }
            .addOnFailureListener { e ->  Log.w(TAG, "Error deleting docuemnt", e)}
    }
}
