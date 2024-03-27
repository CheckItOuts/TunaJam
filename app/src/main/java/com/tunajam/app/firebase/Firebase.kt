import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.json.JSONObject.NULL


class Database {
    // Variables de la class
    private val db = Firebase.firestore


    // Fonctions add

    /**
     * Ajouter un utilisateur à la collection users
     *
     * @param pseudo Pseudo de l'utilisateur à ajouter
     * @param mdp Mot de passe de l'utilisateur
     *
     * @return None
     */
    fun addUser(pseudo : String, mdp : String){
        val user = hashMapOf(
            "pseudo" to pseudo,
            "mdp" to mdp
        )

        db.collection("users").document(pseudo)
            .set(user)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e->Log.d(TAG, "Error writing document", e) }
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


    // Fonctions get

    /**
     * Récupérer les données d'un utilisateur
     *
     * @param pseudo Pseudo de l'utilisateur
     *
     * @return Données de l'utilisateur
     */
    fun getUser(pseudo : String){
        val user = db.collection("users").document(pseudo)

        user.get()
            .addOnSuccessListener { document ->
                if (document != NULL){
                    Log.d(TAG, "DocumentSnapshot data : ${document.data}")
                }
                else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "get failed with ", e)
            }
    }

    /**
     * Récupérer tous les amis d'un utilisateur
     *
     * @param pseudo Pseudo de l'utilisateur
     *
     * @return Amis de l'utilisateur
     */
    fun getFriends(pseudo : String){
        db.collection("users").document(pseudo).collection("friends")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.data}")
                }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error getting documents: ", e)
            }
    }
}
