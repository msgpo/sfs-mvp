package com.vandenbreemen.mobilesecurestoragemvp

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.standardandroidlogging.log.SystemLog

abstract class Model(private val credentials: SFSCredentials) {

    protected lateinit var sfs:SecureFileSystem

    @Throws
    fun init() {
        try {
            this.sfs = object : SecureFileSystem(credentials.fileLocation) {
                override fun getPassword(): SecureString? {
                    return credentials.password
                }
            }

            this.setup()
        } catch (exception: Exception) {
            SystemLog.get().error(javaClass.simpleName, "Failed to load SFS", exception)
            throw exception
        }
    }

    fun close() {
        if (isClosed()) {
            return
        }
        credentials.finalize()
        if(::sfs.isInitialized) {
            sfs.close()
        }

        onClose()
    }

    /**
     * Any additional logic you'd like to perform after the model has been closed
     */
    abstract fun onClose()

    /**
     * Create a copy of the credentials used to create this model.
     */
    fun copyCredentials(): SFSCredentials {
        return credentials.copy()
    }

    private fun isClosed(): Boolean {
        return credentials.finalized()
    }

    /**
     * Do any setup necessary for the model to work.  This method is called once the SFS has been initialized
     */
    protected abstract fun setup()
}