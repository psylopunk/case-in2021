package iooojik.casein.localData.childs

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ChildModel (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id : Long? = null,

    @ColumnInfo(name = "fullName")
    val fullName: String? = null,

    @ColumnInfo(name = "login")
    val login: String? = null,

    @ColumnInfo(name = "parentName")
    val parentName: String? = null,

    @ColumnInfo(name = "parentId")
    val parentId: String? = null,

    @ColumnInfo(name = "created")
    val created: String? = null,

    @ColumnInfo(name = "model_id")
    val modelId: String? = null
)