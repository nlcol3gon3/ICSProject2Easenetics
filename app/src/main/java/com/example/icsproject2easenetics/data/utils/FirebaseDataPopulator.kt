package com.example.icsproject2easenetics.utils

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseDataPopulator {
    private val db = FirebaseFirestore.getInstance()

    // Make these methods public
    suspend fun populateAllData(): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                // Populate modules and wait for completion
                val modulesSuccess = populateModules()
                if (!modulesSuccess) return@withContext false

                // Populate lessons and wait for completion
                val lessonsSuccess = populateLessons()
                if (!lessonsSuccess) return@withContext false

                // Populate quiz questions and wait for completion
                val questionsSuccess = populateQuizQuestions()
                if (!questionsSuccess) return@withContext false

                // Create a test document to verify connection
                createConnectionTest()

                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun populateModules(): Boolean {
        return try {
            val modules = listOf(
                hashMapOf(
                    "moduleId" to "module_1",
                    "title" to "Smartphone Fundamentals (The Basics)",
                    "description" to "This module is about removing the initial fear and building a foundation of confidence.",
                    "order" to 1,
                    "icon" to "📱",
                    "totalLessons" to 5
                ),
                hashMapOf(
                    "moduleId" to "module_2",
                    "title" to "Communication and Connection (Local Essentials)",
                    "description" to "This module focuses on the most popular ways Kenyans connect with family and friends.",
                    "order" to 2,
                    "icon" to "💬",
                    "totalLessons" to 3
                ),
                hashMapOf(
                    "moduleId" to "module_3",
                    "title" to "Mobile Money (M-Pesa) – The Kenyan Wallet",
                    "description" to "This module is critical for financial independence and is a cornerstone of Kenyan digital life.",
                    "order" to 3,
                    "icon" to "💰",
                    "totalLessons" to 4
                ),
                hashMapOf(
                    "moduleId" to "module_4",
                    "title" to "Online Safety and Security (Empowerment)",
                    "description" to "This module is vital for building trust and protecting seniors from common local scams.",
                    "order" to 4,
                    "icon" to "🔒",
                    "totalLessons" to 3
                ),
                hashMapOf(
                    "moduleId" to "module_5",
                    "title" to "Government & Essential Services (eCitizen & KRA)",
                    "description" to "This module directly addresses key government portals, which is essential for civic empowerment.",
                    "order" to 5,
                    "icon" to "🏛️",
                    "totalLessons" to 5
                )
            )

            // Use await() to wait for each operation to complete
            modules.forEach { module ->
                db.collection("modules").document(module["moduleId"] as String)
                    .set(module)
                    .await() // This waits for the operation to complete
                println("✅ Module ${module["moduleId"]} added successfully")
            }
            true
        } catch (e: Exception) {
            println("❌ Error adding modules: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    suspend fun populateLessons(): Boolean {
        return try {
            val lessons = listOf(
                // ==================== MODULE 1: SMARTPHONE FUNDAMENTALS ====================
                hashMapOf(
                    "lessonId" to "lesson_1_1",
                    "moduleId" to "module_1",
                    "title" to "Meet Your Smartphone",
                    "objective" to "To understand the physical parts of a smartphone and basic navigation.",
                    "description" to "Learn the physical parts and basic navigation of your smartphone",
                    "content" to """
                        # Meet Your Smartphone
                        
                        Welcome to your new smartphone! Let's get familiar with the basic parts:
                        
                        ## 📱 Physical Parts:
                        • **Power Button**: Usually on the right side - turns phone on/off
                        • **Volume Buttons**: On the side - control sound volume  
                        • **Charging Port**: At the bottom - for charging cable
                        • **Screen**: The main display you touch
                        • **Front Camera**: For selfies and video calls
                        • **Back Camera**: For taking photos of everything else
                        
                        ## 🔋 First Steps:
                        1. **Charging**: Plug in your phone overnight for first use
                        2. **Turning On**: Press and hold the power button for 3 seconds
                        3. **Unlocking**: Swipe up or enter your PIN/pattern
                        
                        ## 👆 Touchscreen Basics:
                        • **Tap**: Lightly touch the screen to select apps or options
                        • **Swipe**: Move finger across screen to scroll through content
                        • **Pinch**: Use two fingers to zoom in/out on photos and maps
                        
                        ## 💡 Pro Tip:
                        Don't worry about breaking it! Smartphone screens are designed to handle normal touch.
                        Take your time to explore - you'll get comfortable quickly!
                    """.trimIndent(),
                    "duration" to 15,
                    "difficulty" to "BEGINNER",
                    "order" to 1,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),
                hashMapOf(
                    "lessonId" to "lesson_1_2",
                    "moduleId" to "module_1",
                    "title" to "Making and Receiving Phone Calls",
                    "objective" to "To use the phone for its most basic function: calling.",
                    "description" to "Learn how to make and receive calls on your smartphone",
                    "content" to """
                        # Making and Receiving Phone Calls
                        
                        Your smartphone's most important function is making calls. Let's learn how:
                        
                        ## 📞 Making a Call:
                        1. **Open Phone App**: Tap the phone icon (usually green)
                        2. **Dial Number**: Use the keypad to enter the number
                        3. **Make Call**: Tap the green call button
                        4. **End Call**: Tap the red end button when finished
                        
                        ## 📲 Receiving a Call:
                        • **Answer**: Swipe the green button to the right to answer
                        • **Decline**: Swipe the red button to the left to decline  
                        • **Ignore**: Let it ring if you're busy - it goes to voicemail
                        
                        ## 📞 Call History:
                        • **Recent**: See missed, received, and dialed calls
                        • **Contacts**: Saved numbers for quick calling
                        • **Favorites**: Pin important contacts for one-tap calling
                        
                        ## 👥 Using Contacts:
                        • Save frequently called numbers as contacts
                        • Tap a contact name to call them directly
                        • Add photos to recognize callers easily
                        
                        ## 💡 Kenyan Tip:
                        Always check the number before calling to avoid calling wrong numbers!
                        Practice calling a family member to get comfortable!
                    """.trimIndent(),
                    "duration" to 20,
                    "difficulty" to "BEGINNER",
                    "order" to 2,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),
                hashMapOf(
                    "lessonId" to "lesson_1_3",
                    "moduleId" to "module_1",
                    "title" to "The Contacts (Anwani) App",
                    "objective" to "To manage a digital address book.",
                    "description" to "Learn how to save and manage contacts on your phone",
                    "content" to """
                        # The Contacts (Anwani) App
                        
                        Your phone's contacts are your digital address book - let's learn to use it!
                        
                        ## 👤 Saving a New Contact:
                        1. **Open Contacts**: Tap the contacts app (usually a person icon)
                        2. **Add Contact**: Tap the + or "Add Contact" button
                        3. **Enter Details**: 
                           - Name: Jina la mtu
                           - Phone: Nambari ya simu
                           - (Optional) Email, address, photo
                        4. **Save**: Tap save or checkmark
                        
                        ## 🔍 Finding Contacts:
                        • **Search**: Type name in search bar at top
                        • **Scroll**: Swipe up/down to browse all contacts  
                        • **Alphabet**: Use A-Z index on the side for quick jumping
                        
                        ## ✏️ Editing Contacts:
                        • Tap on any contact to view details
                        • Tap edit (pencil icon) to make changes
                        • Update phone numbers, names, or add photos
                        
                        ## 🗑️ Deleting Contacts:
                        • Open contact details
                        • Tap menu (3 dots) → Delete
                        • Confirm deletion
                        
                        ## 💡 Why Save Contacts?
                        • **Safety**: Know who's calling before answering
                        • **Convenience**: One-tap calling to saved numbers  
                        • **Organization**: All numbers in one place
                        • **Emergency**: Quick access to family and important numbers
                        
                        ## 🇰🇪 Kenyan Tip:
                        Save important numbers like:
                        - Family members
                        - Your doctor/clinic  
                        - Emergency contacts
                        - M-Pesa customer care (100)
                    """.trimIndent(),
                    "duration" to 18,
                    "difficulty" to "BEGINNER",
                    "order" to 3,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),
                hashMapOf(
                    "lessonId" to "lesson_1_4",
                    "moduleId" to "module_1",
                    "title" to "Sending and Reading SMS (Messages)",
                    "objective" to "To communicate using basic text messages.",
                    "description" to "Learn how to send and read text messages",
                    "content" to """
                        # Sending and Reading SMS (Messages)
                        
                        SMS (Short Message Service) lets you send text messages to any phone number!
                        
                        ## 💬 Finding Messages App:
                        • Look for "Messages" app (usually green or blue bubble icon)
                        • Tap to open - you'll see your message conversations
                        
                        ## 📖 Reading a Message:
                        • New messages appear in your inbox
                        • Tap any conversation to read messages
                        • Unread messages have a blue dot or bold text
                        
                        ## 📝 Replying to a Message:
                        1. Open the conversation
                        2. Tap the text box at bottom
                        3. Type your message using keyboard
                        4. Tap send (paper plane icon)
                        
                        ## 🆕 Starting New Message:
                        1. Tap compose button (+ or pencil icon)
                        2. Type phone number OR select from contacts
                        3. Type your message
                        4. Tap send
                        
                        ## 💰 SMS vs WhatsApp:
                        • **SMS**: Costs money (about 1 KSH per message)
                        • **WhatsApp**: FREE over Wi-Fi or data bundles
                        • **Use SMS** for: Banks, alerts, some businesses
                        • **Use WhatsApp** for: Family, friends, free chatting
                        
                        ## 📱 Message Features:
                        • **Group Messages**: Send to multiple people
                        • **Attachments**: Send photos (costs extra for SMS)
                        • **Delivery Report**: See if message was delivered
                        
                        ## 💡 Kenyan Tip:
                        Use WhatsApp for most family communication to save money!
                        Only use SMS when necessary or for official communications.
                    """.trimIndent(),
                    "duration" to 22,
                    "difficulty" to "BEGINNER",
                    "order" to 4,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),
                hashMapOf(
                    "lessonId" to "lesson_1_5",
                    "moduleId" to "module_1",
                    "title" to "Connecting to the Internet (Wi-Fi vs Data Bundles)",
                    "objective" to "To understand how the phone gets online.",
                    "description" to "Learn about Wi-Fi and mobile data for internet access",
                    "content" to """
                        # Connecting to the Internet (Wi-Fi vs Data Bundles)
                        
                        Your smartphone needs internet for WhatsApp, browsing, and many useful features!
                        
                        ## 📶 What is Wi-Fi?
                        • Wireless internet connection
                        • Usually FREE at home, office, or restaurants
                        • Fast and unlimited (within range)
                        • Safe for private information
                        
                        ## 🔌 Connecting to Wi-Fi:
                        1. **Open Settings** → **Wi-Fi**
                        2. **Turn on Wi-Fi** (toggle switch)
                        3. **Select network** from list
                        4. **Enter password** if required
                        5. **Tap Connect** - you're online!
                        
                        ## 📱 What is Mobile Data?
                        • Internet through your SIM card
                        • Uses "data bundles" you buy
                        • Works anywhere with network signal
                        • Costs money based on usage
                        
                        ## 💰 Buying Data Bundles:
                        • **Safaricom**: Dial *544# → Data Bundles
                        • **Airtel**: Dial *544# → Data Services  
                        • **Other networks**: Similar codes
                        • Choose bundle based on your needs
                        
                        ## 🔄 Turning Data On/Off:
                        • **Swipe down** from top of screen
                        • Tap **Mobile Data** icon to toggle
                        • **Blue** = ON, **Gray** = OFF
                        
                        ## 💡 Money-Saving Tips:
                        • Use **Wi-Fi** at home to save data costs
                        • Turn **OFF mobile data** when not using internet
                        • Buy **night bundles** for cheaper rates (8PM-6AM)
                        • Use **WhatsApp calls** over Wi-Fi instead of regular calls
                        
                        ## 🇰🇪 Kenyan Networks:
                        • **Safaricom**: *544# for bundles
                        • **Airtel**: *544# for data  
                        • **Telkom**: *444# for services
                    """.trimIndent(),
                    "duration" to 25,
                    "difficulty" to "BEGINNER",
                    "order" to 5,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),

                // ==================== MODULE 2: COMMUNICATION ====================
                hashMapOf(
                    "lessonId" to "lesson_2_1",
                    "moduleId" to "module_2",
                    "title" to "Introduction to WhatsApp",
                    "objective" to "To set up and understand the primary communication app in Kenya.",
                    "description" to "Learn about WhatsApp setup and basic features",
                    "content" to """
                        # Introduction to WhatsApp
                        
                        WhatsApp is Kenya's most popular messaging app - let's get started!
                        
                        ## 📱 What is WhatsApp?
                        • **Free messaging** app (uses Wi-Fi or data)
                        • Send texts, photos, videos, and voice messages
                        • Make free voice and video calls
                        • **Very popular** with family and friends in Kenya
                        
                        ## 🔧 Setting Up WhatsApp:
                        1. **Download**: Get WhatsApp from Google Play Store
                        2. **Agree**: Accept terms and conditions
                        3. **Verify**: Enter your phone number for verification
                        4. **Profile**: Add your name and photo
                        5. **Contacts**: WhatsApp finds your contacts automatically
                        
                        ## 💬 Basic Features:
                        • **Chats**: One-on-one conversations
                        • **Groups**: Family or community group chats
                        • **Status**: Share updates that disappear in 24 hours
                        • **Calls**: Free voice and video calls
                        
                        ## 🔐 Privacy Settings:
                        • **Last Seen**: Who can see when you were last online
                        • **Profile Photo**: Who can see your picture
                        • **Status**: Who can see your updates
                        • **Block**: Block unwanted contacts
                        
                        ## 💡 Kenyan Tip:
                        WhatsApp is perfect for staying connected with children and grandchildren abroad!
                        It's completely free for messaging and calls over Wi-Fi.
                    """.trimIndent(),
                    "duration" to 20,
                    "difficulty" to "BEGINNER",
                    "order" to 1,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),
                hashMapOf(
                    "lessonId" to "lesson_2_2",
                    "moduleId" to "module_2",
                    "title" to "Mastering WhatsApp Communication",
                    "objective" to "To send and receive different types of messages.",
                    "description" to "Learn advanced WhatsApp messaging features",
                    "content" to """
                        # Mastering WhatsApp Communication
                        
                        Now that you have WhatsApp, let's master all the ways to communicate!
                        
                        ## 💬 Sending Text Messages:
                        1. **Open WhatsApp** → **Chats** tab
                        2. **Tap new chat** (green message icon)
                        3. **Select contact** from your list
                        4. **Type message** and tap send
                        
                        ## 📸 Sending Photos:
                        • **Camera**: Tap camera icon to take new photo
                        • **Gallery**: Tap attachment icon → Gallery → Select photo
                        • **Caption**: Add text to describe your photo
                        
                        ## 🎙️ Voice Notes (Sauti):
                        • **Hold microphone** icon while speaking
                        • **Release** to send voice message
                        • **Swipe up** to cancel if you change your mind
                        • **Perfect** for those who don't like typing!
                        
                        ## 👥 Group Chats:
                        • **Create Group**: Menu → New group → Add participants
                        • **Family Groups**: Perfect for family updates
                        • **Community Groups**: Neighborhood or church groups
                        • **Mute Groups**: Silence noisy groups when needed
                        
                        ## 🔔 Notifications:
                        • **Custom tones** for different contacts
                        • **Mute chats** that are too active
                        • **Popup notifications** for important messages
                        
                        ## 💡 Kenyan Tip:
                        Voice notes are very popular in Kenya - they feel more personal than text!
                        Use them to share stories or give detailed instructions.
                    """.trimIndent(),
                    "duration" to 25,
                    "difficulty" to "BEGINNER",
                    "order" to 2,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),
                hashMapOf(
                    "lessonId" to "lesson_2_3",
                    "moduleId" to "module_2",
                    "title" to "WhatsApp Voice and Video Calls",
                    "objective" to "To make free calls to family and friends.",
                    "description" to "Learn how to make WhatsApp calls for free",
                    "content" to """
                        # WhatsApp Voice and Video Calls
                        
                        Make free calls to see and talk to family anywhere in the world!
                        
                        ## 📞 WhatsApp Voice Calls:
                        1. **Open chat** with the person
                        2. **Tap phone icon** at top right
                        3. **Wait for connection** - it will ring on their phone
                        4. **Talk** like a normal phone call
                        5. **Tap red button** to end call
                        
                        ## 📹 WhatsApp Video Calls:
                        1. **Open chat** with the person
                        2. **Tap camera icon** at top right
                        3. **Wait for them to answer**
                        4. **See each other** and talk face-to-face
                        5. **Tap red button** to end call
                        
                        ## 💰 Why WhatsApp Calls Are Free:
                        • **Uses internet** (Wi-Fi or data bundles)
                        • **No airtime** needed
                        • **International calls** same price as local
                        • **Perfect** for calling children abroad
                        
                        ## 📶 Call Quality Tips:
                        • **Use Wi-Fi** for best quality
                        • **Good signal** ensures clear calls
                        • **Headphones** help with echo
                        • **Well-lit room** for better video
                        
                        ## 🔒 Answering Calls:
                        • **Incoming call** shows caller name/photo
                        • **Green button** to answer
                        • **Red button** to decline
                        • **Message** option if you're busy
                        
                        ## 💡 Kenyan Tip:
                        WhatsApp video calls are perfect for seeing grandchildren grow up!
                        Even if they live far away, you can watch them play and learn.
                    """.trimIndent(),
                    "duration" to 22,
                    "difficulty" to "BEGINNER",
                    "order" to 3,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),

                // ==================== MODULE 3: M-PESA ====================
                hashMapOf(
                    "lessonId" to "lesson_3_1",
                    "moduleId" to "module_3",
                    "title" to "M-Pesa Basics: Your Digital Wallet",
                    "objective" to "To understand and securely access your M-Pesa account.",
                    "description" to "Learn about M-Pesa and how to check your balance",
                    "content" to """
                        # M-Pesa Basics: Your Digital Wallet
                        
                        M-Pesa is Kenya's revolutionary mobile money service that turns your phone into a wallet!
                        
                        ## 💰 What is M-Pesa?
                        • **Mobile money** service by Safaricom
                        • Your **phone becomes your wallet**
                        • Send and receive money safely
                        • Pay bills and buy goods
                        
                        ## 🔐 Accessing M-Pesa:
                        • **Safaricom Menu**: Main menu → M-Pesa
                        • **USSD Code**: Dial *334# from your phone
                        • **M-Pesa App**: Download from Play Store (optional)
                        
                        ## 💵 Checking Balance:
                        1. Dial *334# from your phone
                        2. Select **"My Account"**
                        3. Select **"Check Balance"**
                        4. Enter your **M-Pesa PIN**
                        5. See your balance instantly
                        
                        ## 🔒 M-Pesa PIN Security:
                        • **Never share** your PIN with anyone
                        • **Safaricom will NEVER** ask for your PIN
                        • **Memorize** your PIN - don't write it down
                        • **Change PIN** regularly for security
                        
                        ## 📊 Mini Statement:
                        • See your last 10 transactions
                        • Dial *334# → My Account → Mini Statement
                        • Helps track your spending
                        • Free service from Safaricom
                        
                        ## 💡 Kenyan Tip:
                        Your M-Pesa PIN is like your ATM card PIN - keep it secret always!
                        Only enter it when YOU initiate an M-Pesa transaction.
                    """.trimIndent(),
                    "duration" to 25,
                    "difficulty" to "BEGINNER",
                    "order" to 1,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),
                hashMapOf(
                    "lessonId" to "lesson_3_2",
                    "moduleId" to "module_3",
                    "title" to "Sending Money and Buying Airtime",
                    "objective" to "To perform the most common M-Pesa transactions.",
                    "description" to "Learn how to send money and buy airtime with M-Pesa",
                    "content" to """
                        # Sending Money and Buying Airtime
                        
                        Master the two most common M-Pesa transactions used every day in Kenya!
                        
                        ## 💸 Sending Money to Another Number:
                        1. Dial *334# → **"Send Money"**
                        2. Enter **recipient's phone number**
                        3. Enter **amount** to send
                        4. Enter your **M-Pesa PIN** to confirm
                        5. Wait for **confirmation message**
                        
                        ## ✅ Name Confirmation:
                        • **Always check** the name that appears
                        • **Confirm** it's the right person
                        • **Cancel** if name doesn't match
                        • This prevents sending to wrong numbers
                        
                        ## 📞 Buying Airtime:
                        • **For yourself**: *334# → "Buy Airtime" → Enter amount
                        • **For others**: *334# → "Buy Airtime and Bundles for Others"
                        • **Instant delivery** - no waiting
                        • **No extra charges** - you pay exactly the amount
                        
                        ## 💰 Transaction Limits:
                        • **Minimum**: 10 KSH for most transactions
                        • **Maximum**: 150,000 KSH per transaction
                        • **Daily limit**: 300,000 KSH total
                        • **Fuliza**: Up to your approved limit
                        
                        ## 📱 Confirmation Messages:
                        • **Always save** M-Pesa confirmation messages
                        • **Proof** of transaction if needed
                        • **Record keeping** for your finances
                        • **Security** - you know when money moves
                        
                        ## 💡 Kenyan Tip:
                        Always double-check the phone number before sending money!
                        One wrong digit could send your money to a stranger.
                    """.trimIndent(),
                    "duration" to 28,
                    "difficulty" to "BEGINNER",
                    "order" to 2,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),
                hashMapOf(
                    "lessonId" to "lesson_3_3",
                    "moduleId" to "module_3",
                    "title" to "Paying Bills with Lipa na M-Pesa",
                    "objective" to "To gain independence in paying household bills.",
                    "description" to "Learn how to pay bills using M-Pesa Paybill",
                    "content" to """
                        # Paying Bills with Lipa na M-Pesa
                        
                        Pay all your bills from home without visiting offices or banks!
                        
                        ## 🏠 Common Bills You Can Pay:
                        • **KPLC** (Kenya Power) - electricity tokens
                        • **Nairobi Water** - water bills
                        • **DStv/GoTV** - TV subscriptions
                        • **NHIF** - health insurance
                        • **School fees** - many schools now accept M-Pesa
                        
                        ## 💳 Paybill vs Buy Goods:
                        • **Paybill**: For companies and organizations
                        • **Buy Goods**: For shops and businesses
                        • **Different numbers** for each service
                        • **Account number** required for Paybill
                        
                        ## ⚡ Paying KPLC (Electricity):
                        1. Dial *334# → **"Lipa na M-Pesa"**
                        2. Select **"Pay Bill"**
                        3. Enter **Paybill number: 888888**
                        4. Enter **Account Number**: (Your meter number)
                        5. Enter **Amount** to pay
                        6. Enter **M-Pesa PIN** to confirm
                        
                        ## 💧 Paying Water Bill:
                        • **Nairobi Water**: Paybill 
                        • **Account**: Your water account number
                        • **Amount**: Your bill amount
                        • **Confirmation**: Save the message as receipt
                        
                        ## 📺 Paying TV Subscription:
                        • **DStv**: Paybill 955500
                        • **GoTV**: Paybill 955500
                        • **Account**: Your smartcard number
                        • **Instant activation** - no waiting
                        
                        ## 🏥 Paying NHIF:
                        • **Paybill**: 200222
                        • **Account**: Your ID number
                        • **Amount**: Monthly contribution (500 KSH)
                        • **Keep receipt** for your records
                        
                        ## 💡 Kenyan Tip:
                        Always save M-Pesa confirmation messages as proof of payment!
                        They serve as your receipt if there's any dispute.
                    """.trimIndent(),
                    "duration" to 30,
                    "difficulty" to "BEGINNER",
                    "order" to 3,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),
                hashMapOf(
                    "lessonId" to "lesson_3_4",
                    "moduleId" to "module_3",
                    "title" to "Withdrawing and Depositing Money",
                    "objective" to "To move physical cash in and out of your M-Pesa account.",
                    "description" to "Learn how to use M-Pesa agents for cash transactions",
                    "content" to """
                        # Withdrawing and Depositing Money
                        
                        Convert your digital M-Pesa money to physical cash and vice versa!
                        
                        ## 🏪 M-Pesa Agents:
                        • **Everywhere** in Kenya - shops, kiosks, supermarkets
                        • **Look for** Safaricom or M-Pesa signs
                        • **Agent number** displayed at their shop
                        • **Open** from early morning to late evening
                        
                        ## 💵 Withdrawing Cash:
                        1. Go to any M-Pesa agent
                        2. Tell them **"Withdraw"**
                        3. Provide your **phone number**
                        4. Tell them the **amount**
                        5. Show your **ID** (required by law)
                        6. Enter your **M-Pesa PIN** on your phone
                        7. Receive cash and receipt
                        
                        ## 💰 Depositing Cash:
                        1. Go to M-Pesa agent with cash
                        2. Tell them **"Deposit"** 
                        3. Provide your **phone number**
                        4. Give them the **cash amount**
                        5. Wait for **confirmation message**
                        6. Get receipt from agent
                        
                        ## 🔒 Agent Safety:
                        • **Check agent number** matches their display
                        • **Count your cash** before leaving
                        • **Keep receipts** for all transactions
                        • **Report issues** to Safaricom immediately
                        
                        ## 💸 Agent Charges:
                        • **Withdrawal fee**: Depends on amount
                        • **Deposit**: Usually free or small fee
                        • **Fuliza**: Interest charges apply
                        • **Always confirm** charges before transaction
                        
                        ## 💡 Kenyan Tip:
                        Build relationship with agents near your home for better service!
                        They'll recognize you and provide faster, more reliable service.
                    """.trimIndent(),
                    "duration" to 25,
                    "difficulty" to "BEGINNER",
                    "order" to 4,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),

                // ==================== MODULE 4: ONLINE SAFETY ====================
                hashMapOf(
                    "lessonId" to "lesson_4_1",
                    "moduleId" to "module_4",
                    "title" to "Spotting Common M-Pesa Scams",
                    "objective" to "To identify and ignore the most common scams in Kenya.",
                    "description" to "Learn how to recognize and avoid M-Pesa fraud",
                    "content" to """
                        # Spotting Common M-Pesa Scams
                        
                        Protect yourself from fraudsters with these important safety tips!
                        
                        ## 🚨 The 'Wrong Number' Scam:
                        • **What happens**: You get fake M-Pesa message + call
                        • **Scammer says**: "I sent money by mistake, please return"
                        • **Reality**: The message is fake, no money was sent
                        • **Protection**: Never send money back. Real senders can reverse transactions themselves.
                        
                        ## 📱 The 'Fuliza' Scam:
                        • **What happens**: Fake message "Your Fuliza limit increased"
                        • **Contains**: Suspicious link to click
                        • **Reality**: Safaricom never sends links in M-Pesa messages
                        • **Protection**: Never click links in unexpected messages
                        
                        ## 📞 Impersonation Calls:
                        • **What happens**: Caller pretends to be Safaricom/bank/KRA
                        • **They ask for**: Your M-Pesa PIN, ID number, personal details
                        • **Reality**: Legitimate companies NEVER ask for your PIN
                        • **Protection**: Hang up immediately. Call official numbers to verify.
                        
                        ## 🎁 Fake Prize Scams:
                        • **What happens**: Message says you won lottery/prize
                        • **They ask for**: "Processing fee" to release your prize
                        • **Reality**: Legitimate prizes don't require payment
                        • **Protection**: Never pay money to receive a "prize"
                        
                        ## 🛡️ Safety Rules:
                        1. **Never share** your M-Pesa PIN with anyone
                        2. **Safaricom will NEVER** ask for your PIN
                        3. **Verify** unexpected messages by calling official numbers
                        4. **When in doubt**, don't send money!
                        5. **Report** suspicious activity to Safaricom (100)
                        
                        ## 💡 Kenyan Tip:
                        If it sounds too good to be true, it probably is!
                        Trust your instincts - if something feels wrong, it probably is.
                    """.trimIndent(),
                    "duration" to 30,
                    "difficulty" to "BEGINNER",
                    "order" to 1,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),
                hashMapOf(
                    "lessonId" to "lesson_4_2",
                    "moduleId" to "module_4",
                    "title" to "Creating Strong, Memorable Passwords",
                    "objective" to "To secure all online accounts.",
                    "description" to "Learn how to create secure passwords for your accounts",
                    "content" to """
                        # Creating Strong, Memorable Passwords
                        
                        Protect your online accounts from hackers with strong passwords!
                        
                        ## ❌ Bad Passwords to Avoid:
                        • **123456** or **password** - too obvious
                        • Your **name** or **birthdate** - easy to guess
                        • **Phone numbers** - public information
                        • **Simple words** - hackers can guess easily
                        
                        ## ✅ Creating Strong Passwords:
                        • **Use phrases**: "MyGrandsonJomoIs5YearsOld!"
                        • **Mix characters**: Letters + numbers + symbols
                        • **Long passwords**: At least 8 characters
                        • **Personal but not obvious**: "ILoveChapatiOnSunday2024!"
                        
                        ## 🔐 Password Examples:
                        • **Good**: "NairobiRainsInApril@2024"
                        • **Good**: "MyFirstSmartphone#2024" 
                        • **Good**: "GrandchildrenMakeMeHappy123!"
                        • **Bad**: "password", "123456", "john2024"
                        
                        ## 🗝️ Password Management:
                        • **Memorize** - don't write down
                        • **Different passwords** for important accounts
                        • **Change regularly** - every 3-6 months
                        • **Never share** with anyone
                        
                        ## 🔒 Important Accounts to Secure:
                        • **Email account** - most important!
                        • **Social media** (Facebook, WhatsApp)
                        • **Banking apps**
                        • **Government portals** (eCitizen, KRA)
                        
                        ## 💡 Kenyan Tip:
                        Use a phrase about your family that only you would know!
                        Example: "MyGranddaughterMariaStartedSchool2024!"
                    """.trimIndent(),
                    "duration" to 25,
                    "difficulty" to "BEGINNER",
                    "order" to 2,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),
                hashMapOf(
                    "lessonId" to "lesson_4_3",
                    "moduleId" to "module_4",
                    "title" to "Browsing the Web Safely (Google)",
                    "objective" to "To find information online without falling for traps.",
                    "description" to "Learn safe internet browsing practices",
                    "content" to """
                        # Browsing the Web Safely (Google)
                        
                        Search for information online safely and avoid dangerous websites!
                        
                        ## 🔍 Using Google Search:
                        • **Open browser** app (Chrome, Safari)
                        • **Tap search bar** at top
                        • **Type your question**: "How to cook ugali"
                        • **Tap search** or enter button
                        • **Browse results** that appear
                        
                        ## ⚠️ Spotting Ads in Search:
                        • **Look for** "Ad" label before results
                        • **Ads are paid** - companies pay to appear first
                        • **Not necessarily** the best or most accurate
                        • **Scroll past** ads for organic results
                        
                        ## 🔐 Website Safety (HTTPS):
                        • **Look for padlock** 🔒 in address bar
                        • **HTTPS** means secure connection
                        • **Safe for** entering personal information
                        • **Avoid HTTP** sites for sensitive data
                        
                        ## 🎣 Phishing Website Signs:
                        • **Urgent messages**: "Your account will be closed!"
                        • **Too good to be true**: "You won 1 million!"
                        • **Poor spelling/grammar**
                        • **Asks for personal info** unexpectedly
                        
                        ## 🛡️ Safe Browsing Habits:
                        • **Don't click** suspicious links in emails
                        • **Verify information** from multiple sources
                        • **Use trusted websites** for shopping/banking
                        • **Install antivirus** on your phone
                        
                        ## 💡 Kenyan Tip:
                        Not everything online is true! Verify important information.
                        When in doubt, ask a family member to help check.
                    """.trimIndent(),
                    "duration" to 28,
                    "difficulty" to "BEGINNER",
                    "order" to 3,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),

                // ==================== MODULE 5: GOVERNMENT SERVICES ====================
                hashMapOf(
                    "lessonId" to "lesson_5_1",
                    "moduleId" to "module_5",
                    "title" to "What is eCitizen (Gava Mkononi)?",
                    "objective" to "To understand what eCitizen is and how to create an account.",
                    "description" to "Learn about the eCitizen government portal",
                    "content" to """
                        # What is eCitizen (Gava Mkononi)?
                        
                        eCitizen is the official Kenyan government portal for all services!
                        
                        ## 🏛️ What is eCitizen?
                        • **One-stop platform** for all government services
                        • Access services from home using your phone/computer
                        • **Pay for services** securely online
                        • Track application progress in real-time
                        
                        ## 📝 Services Available:
                        • Apply for **passports** and **IDs**
                        • Pay for **business permits** and **licenses**
                        • Access **NSSF** and **NHIF** services
                        • Apply for **driving licenses**
                        • **KRA** tax services
                        • And **many more** services!
                        
                        ## 🔐 Creating an eCitizen Account:
                        1. Visit **www.ecitizen.go.ke**
                        2. Click **"Register"** button
                        3. Enter your **ID number**
                        4. Verify your **mobile number**
                        5. Set a **strong password**
                        6. Confirm your **email address** (optional)
                        7. Start accessing services!
                        
                        ## 💳 Paying on eCitizen:
                        • **M-Pesa** integration
                        • **Credit/debit cards** accepted
                        • **Bank transfers** option
                        • **Instant confirmation** of payments
                        
                        ## 📱 eCitizen Mobile App:
                        • Download **"eCitizen"** from Play Store
                        • Same services as website
                        • **Mobile-friendly** interface
                        • **Push notifications** for updates
                        
                        ## 💡 Kenyan Tip:
                        eCitizen saves you time and travel costs!
                        No more queuing for hours at government offices.
                    """.trimIndent(),
                    "duration" to 25,
                    "difficulty" to "BEGINNER",
                    "order" to 1,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),
                hashMapOf(
                    "lessonId" to "lesson_5_2",
                    "moduleId" to "module_5",
                    "title" to "Accessing NSSF (Pension) on eCitizen",
                    "objective" to "To empower seniors to check their pension and retirement benefits.",
                    "description" to "Learn how to access NSSF services through eCitizen",
                    "content" to """
                        # Accessing NSSF (Pension) on eCitizen
                        
                        Manage your pension and retirement benefits online easily!
                        
                        ## 💼 What is NSSF?
                        • **National Social Security Fund**
                        • **Pension scheme** for Kenyan workers
                        • **Retirement benefits** when you stop working
                        • **Monthly payments** during retirement
                        
                        ## 🔍 Accessing NSSF on eCitizen:
                        1. Login to your **eCitizen account**
                        2. Search for **"NSSF"** in services
                        3. Click on **"NSSF Services"**
                        4. Access your **personal dashboard**
                        
                        ## 📊 Viewing NSSF Statement:
                        • See your **contribution history**
                        • Check your **total balance**
                        • View **projected benefits**
                        • Download **statement** for your records
                        
                        ## 👵 Age/Retirement Benefits:
                        • **Eligibility**: 60 years and above
                        • **Apply online** through eCitizen
                        • **Upload required documents**
                        • **Track application** status
                        • Receive **monthly pension payments**
                        
                        ## 📋 Required Documents:
                        • **Original ID card**
                        • **Passport photo**
                        • **Bank details** for payment
                        • **Employment history** if available
                        
                        ## 💡 Kenyan Tip:
                        Check your NSSF statement regularly to ensure all contributions are recorded!
                        Report any discrepancies immediately to NSSF.
                    """.trimIndent(),
                    "duration" to 28,
                    "difficulty" to "BEGINNER",
                    "order" to 2,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),
                hashMapOf(
                    "lessonId" to "lesson_5_3",
                    "moduleId" to "module_5",
                    "title" to "Accessing NHIF (Health) Services",
                    "objective" to "To manage health insurance and payments.",
                    "description" to "Learn how to use NHIF services through eCitizen",
                    "content" to """
                        # Accessing NHIF (Health) Services
                        
                        Manage your health insurance and contributions online!
                        
                        ## 🏥 What is NHIF?
                        • **National Hospital Insurance Fund**
                        • **Health insurance** for Kenyans
                        • **Hospital coverage** when you're sick
                        • **Monthly contributions** for coverage
                        
                        ## 🔍 Accessing NHIF on eCitizen:
                        1. Login to **eCitizen account**
                        2. Search for **"NHIF"** in services
                        3. Click **"NHIF Services"**
                        4. Access your **member portal**
                        
                        ## 📋 Checking NHIF Status:
                        • See if your **contributions are current**
                        • Check your **coverage status**
                        • View **contribution history**
                        • Download **membership certificate**
                        
                        ## 💳 Paying NHIF Contributions:
                        • **Monthly amount**: 500 KSH (standard rate)
                        • **Pay via M-Pesa**: Through eCitizen
                        • **Auto-debit**: Set up automatic payments
                        • **Payment history**: Track all your payments
                        
                        ## 🏥 Using NHIF Benefits:
                        • **Hospital visits** covered
                        • **Maternity services**
                        • **Chronic illness** treatment
                        • **Specialist consultations**
                        
                        ## 📞 NHIF Support:
                        • **Call center**: 020 272 2581
                        • **Email**: customercare@nhif.or.ke
                        • **Visit**: Nearest NHIF office
                        • **Online**: Live chat on eCitizen
                        
                        ## 💡 Kenyan Tip:
                        Keep your NHIF contributions current to ensure continuous coverage!
                        You never know when you might need medical care.
                    """.trimIndent(),
                    "duration" to 26,
                    "difficulty" to "BEGINNER",
                    "order" to 3,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),
                hashMapOf(
                    "lessonId" to "lesson_5_4",
                    "moduleId" to "module_5",
                    "title" to "Understanding the KRA Portal and your KRA PIN",
                    "objective" to "To understand the basics of the Kenya Revenue Authority (KRA).",
                    "description" to "Learn about KRA PIN and iTax portal",
                    "content" to """
                        # Understanding the KRA Portal and your KRA PIN
                        
                        Learn about Kenya Revenue Authority and your tax obligations!
                        
                        ## 🏛️ What is KRA?
                        • **Kenya Revenue Authority**
                        • **Collects taxes** for the government
                        • **Funds public services** like roads, hospitals, schools
                        • **Every adult Kenyan** should have a KRA PIN
                        
                        ## 🔑 What is a KRA PIN?
                        • **Personal Identification Number** for taxes
                        • **Required for**: Bank accounts, land ownership, business
                        • **Unique to you** - like a tax ID
                        • **Lifetime number** - never changes
                        
                        ## 📋 Finding Your KRA PIN:
                        • **Check old documents**: Bank statements, employment records
                        • **Online checker**: iTax website → "PIN Checker"
                        • **KRA office**: Visit with your ID card
                        • **Call KRA**: 020 4 999 999
                        
                        ## 💻 KRA iTax Portal:
                        • **Website**: itax.kra.go.ke
                        • **Login with**: KRA PIN and password
                        • **Access**: Tax records, file returns, make payments
                        • **Mobile app**: "KRA iTax" available
                        
                        ## 📊 Services on iTax:
                        • **File tax returns**
                        • **Check tax status**
                        • **Make tax payments**
                        • **Download compliance certificates**
                        • **Update personal details**
                        
                        ## 💡 Kenyan Tip:
                        Even if retired, you need a KRA PIN for bank accounts and property!
                        It's better to have one even if you don't currently pay taxes.
                    """.trimIndent(),
                    "duration" to 24,
                    "difficulty" to "BEGINNER",
                    "order" to 4,
                    "hasQuiz" to true,
                    "videoUrl" to null
                ),
                hashMapOf(
                    "lessonId" to "lesson_5_5",
                    "moduleId" to "module_5",
                    "title" to "How to File a KRA 'Nil Return'",
                    "objective" to "To complete the most common and mandatory tax-filing task for many seniors.",
                    "description" to "Learn how to file a nil tax return with KRA",
                    "content" to """
                        # How to File a KRA 'Nil Return'
                        
                        File your annual tax return even if you had no income!
                        
                        ## 📝 What is a 'Nil Return'?
                        • **Declaration** that you had no taxable income
                        • **Mandatory** for everyone with a KRA PIN
                        • **Annual requirement** - due by 30th June each year
                        • **Avoids penalties** for non-filing
                        
                        ## ⚠️ Why File Nil Return?
                        • **Legal requirement** if you have KRA PIN
                        • **Avoid penalties**: 10,000 KSH or 25% of tax due
                        • **Maintain compliance** record
                        • **Required for** some government services
                        
                        ## 💻 Filing Nil Return Online:
                        1. Login to **iTax portal** (itax.kra.go.ke)
                        2. Click **"Returns"** menu
                        3. Select **"File Return"**
                        4. Choose **"Individual"** and tax year
                        5. Select **"Nil Return"** option
                        6. Submit and **get confirmation**
                        
                        ## 📅 Important Dates:
                        • **Filing period**: 1st January - 30th June
                        • **For year**: Previous calendar year
                        • **Example**: File 2024 return by 30th June 2025
                        • **Late filing**: Penalties apply after deadline
                        
                        ## 🆓 No Payment Required:
                        • **Nil return** means no tax to pay
                        • **No charges** for filing
                        • **Keep confirmation** for your records
                        • **Automatic approval** if no income declared
                        
                        ## 📞 Getting Help:
                        • **KRA Helpline**: 020 4 999 999
                        • **Email**: contact@kra.go.ke
                        • **Visit**: Nearest KRA office
                        • **Youth/elderly**: Special assistance available
                        
                        ## 💡 Kenyan Tip:
                        Set a calendar reminder for May each year to file your nil return!
                        Avoid the last-minute rush and potential penalties.
                    """.trimIndent(),
                    "duration" to 30,
                    "difficulty" to "BEGINNER",
                    "order" to 5,
                    "hasQuiz" to true,
                    "videoUrl" to null
                )
            )

            // Use await() to wait for each operation to complete
            lessons.forEach { lesson ->
                db.collection("lessons").document(lesson["lessonId"] as String)
                    .set(lesson)
                    .await() // This waits for the operation to complete
                println("✅ Lesson ${lesson["lessonId"]} added successfully")
            }
            true
        } catch (e: Exception) {
            println("❌ Error adding lessons: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    suspend fun populateQuizQuestions(): Boolean {
        return try {
            val questions = listOf(
                // Questions for Lesson 1_1
                hashMapOf(
                    "questionId" to "question_1_1_1",
                    "lessonId" to "lesson_1_1",
                    "question" to "What is the main function of the power button?",
                    "options" to listOf(
                        "Turns the phone on and off",
                        "Takes photos",
                        "Makes phone calls",
                        "Connects to Wi-Fi"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "The power button is used to turn your smartphone on and off, or to wake it up from sleep mode."
                ),
                hashMapOf(
                    "questionId" to "question_1_1_2",
                    "lessonId" to "lesson_1_1",
                    "question" to "How do you zoom in on a photo?",
                    "options" to listOf(
                        "Tap the screen quickly",
                        "Use two fingers and spread them apart",
                        "Shake the phone",
                        "Press the volume button"
                    ),
                    "correctAnswer" to 1,
                    "explanation" to "The pinch-to-zoom gesture uses two fingers to zoom in (spread apart) or zoom out (pinch together) on photos and maps."
                ),

                // Questions for Lesson 1_2
                hashMapOf(
                    "questionId" to "question_1_2_1",
                    "lessonId" to "lesson_1_2",
                    "question" to "How do you answer an incoming call?",
                    "options" to listOf(
                        "Swipe the green button to the right",
                        "Press the power button",
                        "Shake the phone",
                        "Say 'hello' loudly"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "Swipe the green answer button to the right to accept an incoming call on most smartphones."
                ),
                hashMapOf(
                    "questionId" to "question_1_2_2",
                    "lessonId" to "lesson_1_2",
                    "question" to "Where can you find your call history?",
                    "options" to listOf(
                        "In the Phone app under 'Recent'",
                        "In the Camera app",
                        "In Settings only",
                        "You cannot see call history"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "The Phone app has a 'Recent' tab that shows your missed, received, and dialed calls."
                ),

                // Questions for Lesson 1_3
                hashMapOf(
                    "questionId" to "question_1_3_1",
                    "lessonId" to "lesson_1_3",
                    "question" to "What is the first step to save a new contact?",
                    "options" to listOf(
                        "Open the Contacts app",
                        "Make a phone call first",
                        "Restart your phone",
                        "Take a photo"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "You need to open the Contacts app first to begin saving a new contact."
                ),
                hashMapOf(
                    "questionId" to "question_1_3_2",
                    "lessonId" to "lesson_1_3",
                    "question" to "Why is it important to save contacts?",
                    "options" to listOf(
                        "To know who is calling before answering",
                        "To make your phone faster",
                        "To get free airtime",
                        "To increase storage space"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "Saving contacts helps you identify callers and provides quick access to important numbers."
                ),

                // Questions for Lesson 1_4
                hashMapOf(
                    "questionId" to "question_1_4_1",
                    "lessonId" to "lesson_1_4",
                    "question" to "How much does an SMS typically cost in Kenya?",
                    "options" to listOf(
                        "About 1 KSH per message",
                        "It's always free",
                        "5 KSH per message",
                        "10 KSH per message"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "SMS messages typically cost about 1 KSH each, while WhatsApp messages are free over Wi-Fi or data."
                ),
                hashMapOf(
                    "questionId" to "question_1_4_2",
                    "lessonId" to "lesson_1_4",
                    "question" to "What icon is usually used for the send button in messaging apps?",
                    "options" to listOf(
                        "Paper plane icon",
                        "Green phone icon",
                        "Red end button",
                        "Camera icon"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "The paper plane icon is commonly used as the send button in most messaging applications."
                ),

                // Questions for Lesson 1_5
                hashMapOf(
                    "questionId" to "question_1_5_1",
                    "lessonId" to "lesson_1_5",
                    "question" to "What is the main advantage of using Wi-Fi over mobile data?",
                    "options" to listOf(
                        "It's usually free and unlimited within range",
                        "It works anywhere",
                        "It's faster than all mobile data",
                        "It doesn't need a password"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "Wi-Fi is typically free at home, office, or restaurants and offers unlimited usage within the network range."
                ),
                hashMapOf(
                    "questionId" to "question_1_5_2",
                    "lessonId" to "lesson_1_5",
                    "question" to "How do you turn mobile data on or off?",
                    "options" to listOf(
                        "Swipe down from top and tap Mobile Data icon",
                        "Restart your phone",
                        "Remove the SIM card",
                        "Call customer care"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "You can toggle mobile data on/off by swiping down from the top of the screen and tapping the Mobile Data icon."
                ),

                // Questions for Lesson 2_1
                hashMapOf(
                    "questionId" to "question_2_1_1",
                    "lessonId" to "lesson_2_1",
                    "question" to "What makes WhatsApp different from regular SMS?",
                    "options" to listOf(
                        "It's free to use with internet connection",
                        "It only works with Safaricom",
                        "It costs more than SMS",
                        "It needs special phone"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "WhatsApp uses internet connection (Wi-Fi or data) instead of airtime, making messaging and calls free."
                ),
                hashMapOf(
                    "questionId" to "question_2_1_2",
                    "lessonId" to "lesson_2_1",
                    "question" to "What do you need to verify when setting up WhatsApp?",
                    "options" to listOf(
                        "Your phone number",
                        "Your ID number",
                        "Your email address",
                        "Your home address"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "WhatsApp uses your phone number for verification and to connect you with your contacts who also use WhatsApp."
                ),

                // Questions for Lesson 2_2
                hashMapOf(
                    "questionId" to "question_2_2_1",
                    "lessonId" to "lesson_2_2",
                    "question" to "How do you send a voice note on WhatsApp?",
                    "options" to listOf(
                        "Hold the microphone icon while speaking",
                        "Tap the microphone icon once",
                        "Shake the phone while speaking",
                        "Call the person directly"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "To send a voice note, hold the microphone icon while speaking, then release to send."
                ),
                hashMapOf(
                    "questionId" to "question_2_2_2",
                    "lessonId" to "lesson_2_2",
                    "question" to "What can you do if a WhatsApp group is too active?",
                    "options" to listOf(
                        "Mute the group notifications",
                        "Delete the group",
                        "Block all members",
                        "Uninstall WhatsApp"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "You can mute group notifications to silence noisy groups while still remaining in the group."
                ),

                // Questions for Lesson 2_3
                hashMapOf(
                    "questionId" to "question_2_3_1",
                    "lessonId" to "lesson_2_3",
                    "question" to "Why are WhatsApp calls free?",
                    "options" to listOf(
                        "They use internet instead of airtime",
                        "Safaricom pays for them",
                        "They are government subsidized",
                        "They use satellite connection"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "WhatsApp calls use internet connection (Wi-Fi or data bundles) instead of airtime, making them free."
                ),
                hashMapOf(
                    "questionId" to "question_2_3_2",
                    "lessonId" to "lesson_2_3",
                    "question" to "What icon do you tap to make a WhatsApp video call?",
                    "options" to listOf(
                        "Camera icon",
                        "Phone icon",
                        "Video icon",
                        "Green button"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "Tap the camera icon at the top right of a chat to start a WhatsApp video call."
                ),

                // Questions for Lesson 3_1 (M-Pesa Basics)
                hashMapOf(
                    "questionId" to "question_3_1_1",
                    "lessonId" to "lesson_3_1",
                    "question" to "How do you check your M-Pesa balance?",
                    "options" to listOf(
                        "Dial *334# and follow prompts",
                        "Send an SMS to 100",
                        "Visit a bank",
                        "Ask a friend to check for you"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "Dial *334# from your Safaricom line, then select 'My Account' → 'Check Balance' to see your M-Pesa balance."
                ),
                hashMapOf(
                    "questionId" to "question_3_1_2",
                    "lessonId" to "lesson_3_1",
                    "question" to "Who should know your M-Pesa PIN?",
                    "options" to listOf(
                        "Only you - never share it with anyone",
                        "Your family members",
                        "Safaricom customer care",
                        "M-Pesa agents"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "Your M-Pesa PIN should be kept completely secret. Safaricom will NEVER ask for your PIN."
                ),

                // Questions for Lesson 3_2
                hashMapOf(
                    "questionId" to "question_3_2_1",
                    "lessonId" to "lesson_3_2",
                    "question" to "What should you always check before sending money via M-Pesa?",
                    "options" to listOf(
                        "The recipient's name that appears",
                        "Your account balance first",
                        "The time of day",
                        "Weather forecast"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "Always check the name that appears to confirm you're sending to the right person before entering your PIN."
                ),
                hashMapOf(
                    "questionId" to "question_3_2_2",
                    "lessonId" to "lesson_3_2",
                    "question" to "What is the minimum amount you can send via M-Pesa?",
                    "options" to listOf(
                        "10 KSH",
                        "1 KSH",
                        "50 KSH",
                        "100 KSH"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "The minimum transaction amount for most M-Pesa transactions is 10 KSH."
                ),

                // Questions for Lesson 3_3
                hashMapOf(
                    "questionId" to "question_3_3_1",
                    "lessonId" to "lesson_3_3",
                    "question" to "What is the paybill number for KPLC electricity?",
                    "options" to listOf(
                        "888888",
                        "955500",
                        "100100",
                        "200222"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "888888 is the paybill number for KPLC (Kenya Power) for purchasing electricity tokens."
                ),
                hashMapOf(
                    "questionId" to "question_3_3_2",
                    "lessonId" to "lesson_3_3",
                    "question" to "Why should you save M-Pesa confirmation messages?",
                    "options" to listOf(
                        "As proof of payment and for your records",
                        "To get discounts on next transaction",
                        "To show friends",
                        "They automatically delete after time"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "M-Pesa confirmation messages serve as your receipt and proof of payment if there are any disputes."
                ),

                // Questions for Lesson 3_4
                hashMapOf(
                    "questionId" to "question_3_4_1",
                    "lessonId" to "lesson_3_4",
                    "question" to "What must you show when withdrawing cash from an M-Pesa agent?",
                    "options" to listOf(
                        "Your ID card",
                        "Your birth certificate",
                        "Utility bill",
                        "Bank statement"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "By law, you must show your ID card when withdrawing cash from M-Pesa agents for security purposes."
                ),
                hashMapOf(
                    "questionId" to "question_3_4_2",
                    "lessonId" to "lesson_3_4",
                    "question" to "What should you do before leaving an M-Pesa agent?",
                    "options" to listOf(
                        "Count your cash and check the receipt",
                        "Thank the agent politely",
                        "Make another transaction",
                        "Check your phone signal"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "Always count your cash and check the receipt before leaving the agent to ensure the transaction was correct."
                ),

                // Questions for Lesson 4_1 (Scams)
                hashMapOf(
                    "questionId" to "question_4_1_1",
                    "lessonId" to "lesson_4_1",
                    "question" to "What should you do if someone calls claiming to be from Safaricom and asks for your M-Pesa PIN?",
                    "options" to listOf(
                        "Hang up immediately - it's a scam",
                        "Give them the PIN to verify",
                        "Ask them to call back later",
                        "Share half of your PIN"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "Legitimate companies like Safaricom will NEVER ask for your M-Pesa PIN. Hang up immediately and report the call."
                ),
                hashMapOf(
                    "questionId" to "question_4_1_2",
                    "lessonId" to "lesson_4_1",
                    "question" to "What is a common sign of a fake prize scam?",
                    "options" to listOf(
                        "They ask for a processing fee to release your prize",
                        "They send the prize immediately",
                        "They visit your home with the prize",
                        "They ask for your address to deliver"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "Fake prize scams often ask for a 'processing fee' - legitimate prizes don't require payment to receive."
                ),

                // Questions for Lesson 4_2
                hashMapOf(
                    "questionId" to "question_4_2_1",
                    "lessonId" to "lesson_4_2",
                    "question" to "What makes a password strong?",
                    "options" to listOf(
                        "Using a mix of letters, numbers, and symbols",
                        "Using your birthdate",
                        "Using 'password123'",
                        "Using your phone number"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "Strong passwords use a combination of uppercase/lowercase letters, numbers, and symbols to make them hard to guess."
                ),
                hashMapOf(
                    "questionId" to "question_4_2_2",
                    "lessonId" to "lesson_4_2",
                    "question" to "How often should you change important passwords?",
                    "options" to listOf(
                        "Every 3-6 months",
                        "Never",
                        "Once a year",
                        "Only when you forget them"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "For security, it's recommended to change important passwords every 3-6 months."
                ),

                // Questions for Lesson 4_3
                hashMapOf(
                    "questionId" to "question_4_3_1",
                    "lessonId" to "lesson_4_3",
                    "question" to "What does the padlock icon 🔒 in the address bar mean?",
                    "options" to listOf(
                        "The website has a secure connection",
                        "The website is locked and cannot be accessed",
                        "The website requires payment",
                        "The website is government-owned"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "The padlock icon indicates a secure HTTPS connection, which is safe for entering personal information."
                ),
                hashMapOf(
                    "questionId" to "question_4_3_2",
                    "lessonId" to "lesson_4_3",
                    "question" to "What should you do if a website seems suspicious?",
                    "options" to listOf(
                        "Verify information from multiple sources",
                        "Enter your information quickly",
                        "Ignore the warning signs",
                        "Share it with friends"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "If a website seems suspicious, verify the information from multiple trusted sources before proceeding."
                ),

                // Questions for Lesson 5_1
                hashMapOf(
                    "questionId" to "question_5_1_1",
                    "lessonId" to "lesson_5_1",
                    "question" to "What is eCitizen?",
                    "options" to listOf(
                        "Kenya's official government services portal",
                        "A social media platform",
                        "A mobile banking app",
                        "A shopping website"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "eCitizen is Kenya's official one-stop platform for accessing all government services online."
                ),
                hashMapOf(
                    "questionId" to "question_5_1_2",
                    "lessonId" to "lesson_5_1",
                    "question" to "What do you need to register for eCitizen?",
                    "options" to listOf(
                        "Your ID number and mobile number",
                        "Your passport only",
                        "Your birth certificate",
                        "Your university degree"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "To register for eCitizen, you need your Kenyan ID number and a valid mobile number for verification."
                ),

                // Questions for Lesson 5_2
                hashMapOf(
                    "questionId" to "question_5_2_1",
                    "lessonId" to "lesson_5_2",
                    "question" to "What does NSSF stand for?",
                    "options" to listOf(
                        "National Social Security Fund",
                        "National Savings and Security Fund",
                        "National Service Support Fund",
                        "Nairobi Social Services Fund"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "NSSF stands for National Social Security Fund, which manages pension and retirement benefits in Kenya."
                ),
                hashMapOf(
                    "questionId" to "question_5_2_2",
                    "lessonId" to "lesson_5_2",
                    "question" to "What can you check on your NSSF statement?",
                    "options" to listOf(
                        "Your contribution history and total balance",
                        "Your medical records",
                        "Your voting history",
                        "Your driving offenses"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "Your NSSF statement shows your contribution history, total balance, and projected retirement benefits."
                ),

                // Questions for Lesson 5_3
                hashMapOf(
                    "questionId" to "question_5_3_1",
                    "lessonId" to "lesson_5_3",
                    "question" to "What is the standard monthly NHIF contribution?",
                    "options" to listOf(
                        "500 KSH",
                        "1000 KSH",
                        "200 KSH",
                        "1500 KSH"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "The standard monthly NHIF contribution for most Kenyans is 500 KSH."
                ),
                hashMapOf(
                    "questionId" to "question_5_3_2",
                    "lessonId" to "lesson_5_3",
                    "question" to "Why is it important to keep NHIF contributions current?",
                    "options" to listOf(
                        "To ensure continuous health coverage",
                        "To avoid government fines",
                        "To get tax refunds",
                        "To qualify for loans"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "Keeping NHIF contributions current ensures you have continuous health insurance coverage when you need medical care."
                ),

                // Questions for Lesson 5_4
                hashMapOf(
                    "questionId" to "question_5_4_1",
                    "lessonId" to "lesson_5_4",
                    "question" to "What does KRA stand for?",
                    "options" to listOf(
                        "Kenya Revenue Authority",
                        "Kenya Registration Agency",
                        "Kenya Resources Administration",
                        "Kenya Regulatory Authority"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "KRA stands for Kenya Revenue Authority, the government agency responsible for tax collection."
                ),
                hashMapOf(
                    "questionId" to "question_5_4_2",
                    "lessonId" to "lesson_5_4",
                    "question" to "What is a KRA PIN used for?",
                    "options" to listOf(
                        "Bank accounts, land ownership, and business",
                        "Social media accounts only",
                        "Mobile phone registration",
                        "Supermarket discounts"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "A KRA PIN is required for bank accounts, land ownership, business registration, and other official transactions."
                ),

                // Questions for Lesson 5_5 (KRA Nil Return)
                hashMapOf(
                    "questionId" to "question_5_5_1",
                    "lessonId" to "lesson_5_5",
                    "question" to "What is a 'Nil Return'?",
                    "options" to listOf(
                        "A declaration that you had no taxable income",
                        "A form to get tax refund",
                        "A way to avoid paying taxes",
                        "A special tax rate for seniors"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "A Nil Return is a declaration to KRA that you had no taxable income during the year. It's mandatory for all KRA PIN holders."
                ),
                hashMapOf(
                    "questionId" to "question_5_5_2",
                    "lessonId" to "lesson_5_5",
                    "question" to "When is the deadline for filing annual tax returns?",
                    "options" to listOf(
                        "30th June each year",
                        "31st December each year",
                        "1st January each year",
                        "There is no deadline"
                    ),
                    "correctAnswer" to 0,
                    "explanation" to "The annual deadline for filing tax returns with KRA is 30th June each year for the previous calendar year."
                )
            )

            // Use await() to wait for each operation to complete
            questions.forEach { question ->
                db.collection("quiz_questions").document(question["questionId"] as String)
                    .set(question)
                    .await() // This waits for the operation to complete
                println("✅ Question ${question["questionId"]} added successfully")
            }
            true
        } catch (e: Exception) {
            println("❌ Error adding quiz questions: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    private suspend fun createConnectionTest() {
        try {
            val testDoc = hashMapOf(
                "timestamp" to System.currentTimeMillis(),
                "message" to "Firebase connection test successful",
                "populatorVersion" to "2.0"
            )

            db.collection("connection_tests").document("populator_test")
                .set(testDoc)
                .await()

            println("✅ Connection test document created successfully")
        } catch (e: Exception) {
            println("❌ Error creating connection test: ${e.message}")
            throw e
        }
    }
}