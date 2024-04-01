package com.tunajam.app.data

import com.tunajam.app.model.TunaJamPhoto

/**
* Define a data class to represent a friend
*/
data class Friend(val id: String, val pseudo: String, val isActive: Boolean, val picture:TunaJamPhoto)
object FriendDirectory {
    val friends = mutableListOf<Friend>()
    fun addFriend(id: String, pseudo: String, isActive: Boolean, picture:TunaJamPhoto) {
        val newFriend = Friend(id, pseudo, isActive, picture)
        friends.add(newFriend)
    }
    fun clearFriends(){
        friends.clear()
    }
    fun removeFriend(friend:Friend){
        friends.remove(friend)
    }
}