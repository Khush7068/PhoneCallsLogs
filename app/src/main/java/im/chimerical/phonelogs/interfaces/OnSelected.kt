package im.chimerical.phonelogs.interfaces

import im.chimerical.phonelogs.CallLog
import im.chimerical.phonelogs.Contact

interface OnSelected {
    fun onClick(list: CallLog){}
    fun onContactClick(list: Contact){}
}