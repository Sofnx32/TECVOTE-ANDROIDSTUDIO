package pe.tecvote.enrolamiento

import android.app.Application
import pe.tecvote.enrolamiento.data.ClienteRed

class TecvoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ClienteRed.init(this)
    }
}