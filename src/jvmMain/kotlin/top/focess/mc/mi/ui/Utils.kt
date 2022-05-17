package top.focess.mc.mi.ui

fun substring(str:String, end: Int) = if (str.length < end) str else str.substring(0, end)

fun showTag(tag: Map<String,Any>): String {
    if (tag.size == 1 && tag.containsKey("desRem"))
        return "(" + tag["desRem"].toString() + ")"
    else if (tag.isEmpty())
        return ""
    return tag.toString()
}