package sds

fun main(args: Array<String>) {
    if(args.isEmpty()) {
        throw RuntimeException("please specify target classfiles or jar file.")
    }
    val sds: SDS = SDS(args)
    sds.run()
}