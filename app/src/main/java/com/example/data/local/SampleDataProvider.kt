package com.example.data.local

object SampleDataProvider {

    fun getDefaultCurrentUser(): UserProfileEntity {
        return UserProfileEntity(
            id = "user_me",
            name = "Rahul Rathod (Bhukya)",
            age = 29,
            occupation = "Lead Software Engineer @ MNC",
            location = "Hyderabad, Telangana",
            biodata = "Namaste! I come from a traditional yet progressive Banjara family. I value our rich Lambani cultural roots, celebrate Teej and Sevalal Maharaj Jayanti with reverence. Passionate about technology, badminton, and family gatherings. Looking for an educated, understanding partner with shared cultural values.",
            fullName = "Rahul Rathod (Bhukya)",
            gender = "Male",
            dateOfBirth = "1997-04-15",
            height = "5 ft 10 in (178 cm)",
            maritalStatus = "Never Married",
            motherTongue = "Gor Boli (Banjara)",
            clan = "Rathod",
            subClan = "Bhukya",
            tanda = "Sevalal Tanda, Nalgonda",
            city = "Hyderabad",
            state = "Telangana",
            education = "B.Tech (CSE) - Osmania University",
            annualIncome = "₹24 - 28 LPA",
            workLocation = "HITEC City, Hyderabad",
            diet = "Vegetarian",
            drinking = "No",
            smoking = "No",
            aboutMe = "Namaste! I come from a traditional yet progressive Banjara family. I value our rich Lambani cultural roots, celebrate Teej and Sevalal Maharaj Jayanti with reverence. Passionate about technology, badminton, and family gatherings. Looking for an educated, understanding partner with shared cultural values.",
            familyDetails = "Father: Retired Gazetted Officer (Irrigation Dept); Mother: Homemaker; 1 Elder Sister (Married, M.S in US); Nuclear family with traditional values.",
            partnerPreferences = "Age: 24-28, Height: 5'2\"+, Clan: Pawar, Chauhan, or Jadhav (Non-Rathod per gotra traditions), Education: Graduate / Post Graduate, Location: Telangana / Karnataka / Maharashtra.",
            photoUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600&auto=format&fit=crop&q=80",
            isPhotoBlurred = false,
            isIdentityVerified = true,
            isPhoneVerified = true,
            verificationStatus = "VERIFIED",
            membershipTier = "GOLD",
            isCurrentUser = true,
            isVisibleInSearch = true
        )
    }

    fun getSampleProfiles(): List<UserProfileEntity> {
        return listOf(
            UserProfileEntity(
                id = "prof_001",
                name = "Dr. Priyanka Pawar (Jarpla)",
                age = 28,
                occupation = "Consultant Pediatrician",
                location = "Hyderabad, Telangana",
                biodata = "Doctor by profession with a warm heart for community service. Born and brought up in Hyderabad with strong family values. Enjoy classical music, travel, and traditional Lambani embroidery art. Looking for a supportive partner with respectful outlook.",
                fullName = "Dr. Priyanka Pawar (Jarpla)",
                gender = "Female",
                dateOfBirth = "1998-08-20",
                height = "5 ft 4 in (162 cm)",
                maritalStatus = "Never Married",
                motherTongue = "Gor Boli (Banjara)",
                clan = "Pawar",
                subClan = "Jarpla",
                tanda = "Balaji Tanda, Mahabubnagar",
                city = "Hyderabad",
                state = "Telangana",
                education = "MBBS, MD (Pediatrics) - Gandhi Medical College",
                annualIncome = "₹20 - 25 LPA",
                workLocation = "Continental Hospitals, Hyderabad",
                diet = "Vegetarian",
                drinking = "No",
                smoking = "No",
                aboutMe = "Doctor by profession with a warm heart for community service. Born and brought up in Hyderabad with strong family values. Enjoy classical music, travel, and traditional Lambani embroidery art. Looking for a supportive partner with respectful outlook.",
                familyDetails = "Father: Principal, Govt Degree College; Mother: High School Teacher; 1 Younger Brother (doing B.Tech at NIT Warangal).",
                partnerPreferences = "Age: 28-32, Clan: Rathod, Chauhan, or Jadhav. Well-educated professional who values work-life harmony.",
                photoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=600&auto=format&fit=crop&q=80",
                isPhotoBlurred = false,
                isIdentityVerified = true,
                isPhoneVerified = true,
                verificationStatus = "VERIFIED",
                membershipTier = "ROYAL"
            ),
            UserProfileEntity(
                id = "prof_002",
                name = "Ananya Chauhan (Kramot)",
                age = 26,
                occupation = "Senior Data Scientist @ Tech Firm",
                location = "Bengaluru, Karnataka",
                biodata = "Forward-thinking tech enthusiast who cherishes our Lambani folk traditions and dance. I love trekking, weekend culinary experiments, and books. Seeking an empathetic companion who respects mutual ambitions.",
                fullName = "Ananya Chauhan (Kramot)",
                gender = "Female",
                dateOfBirth = "1999-11-12",
                height = "5 ft 5 in (165 cm)",
                maritalStatus = "Never Married",
                motherTongue = "Gor Boli (Banjara)",
                clan = "Chauhan",
                subClan = "Kramot",
                tanda = "Sant Sevalal Nagar, Bellary",
                city = "Bengaluru",
                state = "Karnataka",
                education = "M.S. in Data Analytics - IIM Bangalore",
                annualIncome = "₹28 - 32 LPA",
                workLocation = "Whitefield, Bengaluru",
                diet = "Eggetarian",
                drinking = "No",
                smoking = "No",
                aboutMe = "Forward-thinking tech enthusiast who cherishes our Lambani folk traditions and dance. I love trekking, weekend culinary experiments, and books. Seeking an empathetic companion who respects mutual ambitions.",
                familyDetails = "Father: Business Owner (Civil Construction); Mother: Homemaker; 1 Elder Brother (Married, Architect in Bengaluru).",
                partnerPreferences = "Age: 27-31, Clan: Rathod, Pawar, or Jadhav. Professional mindset, settled in Bangalore/Hyderabad.",
                photoUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=600&auto=format&fit=crop&q=80",
                isPhotoBlurred = false,
                isIdentityVerified = true,
                isPhoneVerified = true,
                verificationStatus = "VERIFIED",
                membershipTier = "GOLD"
            ),
            UserProfileEntity(
                id = "prof_003",
                name = "Sunita Jadhav (Badavat)",
                age = 29,
                occupation = "Assistant Administrative Officer (Govt)",
                location = "Pune, Maharashtra",
                biodata = "Grounded, disciplined and spiritually inclined. Actively involved in community youth guidance and girl-child education awareness in our Tandas. Seeking an honest, family-oriented partner.",
                fullName = "Sunita Jadhav (Badavat)",
                gender = "Female",
                dateOfBirth = "1997-02-18",
                height = "5 ft 3 in (160 cm)",
                maritalStatus = "Never Married",
                motherTongue = "Gor Boli (Banjara)",
                clan = "Jadhav",
                subClan = "Badavat",
                tanda = "Dhanora Tanda, Yavatmal",
                city = "Pune",
                state = "Maharashtra",
                education = "M.A. (Public Admin), Preparing for UPSC/MPSC",
                annualIncome = "₹12 - 15 LPA",
                workLocation = "Pune Division, Maharashtra",
                diet = "Vegetarian",
                drinking = "No",
                smoking = "No",
                aboutMe = "Grounded, disciplined and spiritually inclined. Actively involved in community youth guidance and girl-child education awareness in our Tandas. Seeking an honest, family-oriented partner.",
                familyDetails = "Father: Senior Zilla Parishad Officer; Mother: Homemaker; 2 Younger Sisters (both pursuing professional degrees).",
                partnerPreferences = "Age: 29-33, Clan: Rathod, Pawar, or Chauhan. Govt or private sector officer with stable career.",
                photoUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=600&auto=format&fit=crop&q=80",
                isPhotoBlurred = false,
                isIdentityVerified = true,
                isPhoneVerified = true,
                verificationStatus = "VERIFIED",
                membershipTier = "SILVER"
            ),
            UserProfileEntity(
                id = "prof_004",
                name = "Kavitha Vadtya (Dharawat)",
                age = 26,
                occupation = "Manager - Corporate Audit @ Big 4",
                location = "Hyderabad, Telangana",
                biodata = "Financial consultant who loves numbers, coffee, and traditional festivities. Believe in equality, open communication, and cultural preservation. Looking for a cheerful life partner.",
                fullName = "Kavitha Vadtya (Dharawat)",
                gender = "Female",
                dateOfBirth = "2000-05-24",
                height = "5 ft 6 in (167 cm)",
                maritalStatus = "Never Married",
                motherTongue = "Gor Boli (Banjara)",
                clan = "Vadtya",
                subClan = "Dharawat",
                tanda = "Ramji Tanda, Warangal",
                city = "Hyderabad",
                state = "Telangana",
                education = "Chartered Accountant (CA) - ICAI",
                annualIncome = "₹22 - 26 LPA",
                workLocation = "Gachibowli, Hyderabad",
                diet = "Vegetarian",
                drinking = "No",
                smoking = "No",
                aboutMe = "Financial consultant who loves numbers, coffee, and traditional festivities. Believe in equality, open communication, and cultural preservation. Looking for a cheerful life partner.",
                familyDetails = "Father: Businessman (Cotton Mills); Mother: Social Worker; 1 Elder Brother (Chartered Accountant).",
                partnerPreferences = "Age: 27-31, Height: 5'8\"+, Clan: Pawar, Rathod, Chauhan. Professional in Finance or Tech.",
                photoUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=600&auto=format&fit=crop&q=80",
                isPhotoBlurred = false,
                isIdentityVerified = true,
                isPhoneVerified = true,
                verificationStatus = "VERIFIED",
                membershipTier = "GOLD"
            ),
            UserProfileEntity(
                id = "prof_005",
                name = "Deepika Banoth (Ade)",
                age = 27,
                occupation = "Interior & Sustainable Architect",
                location = "Hyderabad, Telangana",
                biodata = "Passionate about incorporating sustainable Banjara heritage textiles and crafts into modern architectural spaces. Love sketching, singing folk ballads, and gardening.",
                fullName = "Deepika Banoth (Ade)",
                gender = "Female",
                dateOfBirth = "1998-10-05",
                height = "5 ft 2 in (157 cm)",
                maritalStatus = "Never Married",
                motherTongue = "Gor Boli (Banjara)",
                clan = "Vadtya",
                subClan = "Banoth",
                tanda = "Hathnoora Tanda, Sangareddy",
                city = "Hyderabad",
                state = "Telangana",
                education = "B.Arch (Architecture) - JNAFAU",
                annualIncome = "₹15 - 18 LPA",
                workLocation = "Banjara Hills, Hyderabad",
                diet = "Vegetarian",
                drinking = "No",
                smoking = "No",
                aboutMe = "Passionate about incorporating sustainable Banjara heritage textiles and crafts into modern architectural spaces. Love sketching, singing folk ballads, and gardening.",
                familyDetails = "Father: Civil Contractor; Mother: Homemaker; 1 Elder Brother (Software Engineer).",
                partnerPreferences = "Age: 28-32, Creative or engineering professional with broad mindset and family values.",
                photoUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=600&auto=format&fit=crop&q=80",
                isPhotoBlurred = true, // Demonstrating photo privacy blur feature
                isIdentityVerified = false,
                isPhoneVerified = true,
                verificationStatus = "PENDING",
                membershipTier = "FREE"
            ),
            UserProfileEntity(
                id = "prof_006",
                name = "Vikram Pawar (Jarpla)",
                age = 31,
                occupation = "Principal Hardware Design Engineer",
                location = "Bengaluru, Karnataka",
                biodata = "Tech leader, marathon runner, and proud promoter of Banjara folklore history. Balance a fast-paced career with mindful community engagement. Looking for an educated life partner.",
                fullName = "Vikram Pawar (Jarpla)",
                gender = "Male",
                dateOfBirth = "1995-03-14",
                height = "5 ft 11 in (180 cm)",
                maritalStatus = "Never Married",
                motherTongue = "Gor Boli (Banjara)",
                clan = "Pawar",
                subClan = "Jarpla",
                tanda = "Laxman Tanda, Davanagere",
                city = "Bengaluru",
                state = "Karnataka",
                education = "B.E, M.Tech (VLSI) - RV College of Engg",
                annualIncome = "₹35 - 40 LPA",
                workLocation = "Electronic City, Bengaluru",
                diet = "Non-Vegetarian",
                drinking = "No",
                smoking = "No",
                aboutMe = "Tech leader, marathon runner, and proud promoter of Banjara folklore history. Balance a fast-paced career with mindful community engagement. Looking for an educated life partner.",
                familyDetails = "Father: Retd. Assistant Commissioner of Police; Mother: Homemaker; 1 Younger Sister (Lecturer).",
                partnerPreferences = "Age: 26-29, Clan: Rathod, Chauhan, Jadhav. Graduate or Post Graduate.",
                photoUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=600&auto=format&fit=crop&q=80",
                isPhotoBlurred = false,
                isIdentityVerified = true,
                isPhoneVerified = true,
                verificationStatus = "VERIFIED",
                membershipTier = "ROYAL"
            )
        )
    }

    fun getInitialInterests(): List<InterestEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            InterestEntity(
                id = "int_001",
                senderId = "prof_001", // Dr. Priyanka Pawar sent interest to current user
                receiverId = "user_me",
                status = "PENDING",
                personalNote = "Namaste Rahul ji. I reviewed your profile and found our family values and career aspirations very aligned. Would be glad to connect.",
                createdAt = now - 3600000 * 5,
                updatedAt = now - 3600000 * 5
            ),
            InterestEntity(
                id = "int_002",
                senderId = "prof_002", // Ananya Chauhan sent interest to current user
                receiverId = "user_me",
                status = "ACCEPTED",
                personalNote = "Hello! Both our families are based in Hyderabad/Bangalore. Let us talk!",
                createdAt = now - 86400000 * 2,
                updatedAt = now - 86400000
            ),
            InterestEntity(
                id = "int_003",
                senderId = "user_me",
                receiverId = "prof_004", // Current user sent to Kavitha
                status = "PENDING",
                personalNote = "Respected Kavitha ji, your professional accomplishments in CA are inspiring. Looking forward to knowing your family.",
                createdAt = now - 3600000 * 12,
                updatedAt = now - 3600000 * 12
            )
        )
    }

    fun getInitialShortlists(): List<ShortlistEntity> {
        return listOf(
            ShortlistEntity(
                id = "sh_001",
                userId = "user_me",
                targetProfileId = "prof_001"
            ),
            ShortlistEntity(
                id = "sh_002",
                userId = "user_me",
                targetProfileId = "prof_003"
            )
        )
    }

    fun getInitialMessages(): List<MessageEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            MessageEntity(
                id = "msg_001",
                senderId = "prof_002",
                receiverId = "user_me",
                content = "Namaste Rahul! Happy to connect on Banjara Matrimony.",
                isRead = true,
                createdAt = now - 3600000 * 3
            ),
            MessageEntity(
                id = "msg_002",
                senderId = "user_me",
                receiverId = "prof_002",
                content = "Namaste Ananya ji! Pleased to connect. I saw that you studied at IIM-B, that's wonderful!",
                isRead = true,
                createdAt = now - 3600000 * 2
            ),
            MessageEntity(
                id = "msg_003",
                senderId = "prof_002",
                receiverId = "user_me",
                content = "Thank you! Yes, data science has been exciting. When do your parents plan to visit Bangalore?",
                isRead = true,
                createdAt = now - 3600000
            )
        )
    }

    fun getInitialNotifications(): List<NotificationEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            NotificationEntity(
                id = "notif_001",
                userId = "user_me",
                title = "New Interest Received",
                message = "Dr. Priyanka Pawar (Jarpla) has expressed interest in your profile.",
                type = "INTEREST",
                isRead = false,
                createdAt = now - 3600000 * 4
            ),
            NotificationEntity(
                id = "notif_002",
                userId = "user_me",
                title = "Interest Accepted!",
                message = "Ananya Chauhan (Kramot) accepted your interest. You can now chat securely.",
                type = "CONNECTION",
                isRead = true,
                createdAt = now - 86400000
            ),
            NotificationEntity(
                id = "notif_003",
                userId = "user_me",
                title = "Profile Verification Complete",
                message = "Your Government ID and Phone have been successfully verified with a Gold Badge.",
                type = "VERIFICATION",
                isRead = true,
                createdAt = now - 86400000 * 3
            )
        )
    }

    fun getInitialReports(): List<ReportEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            ReportEntity(
                id = "rep_001",
                reporterId = "prof_003",
                targetProfileId = "prof_005",
                targetName = "Deepika Banoth",
                category = "Unclear Photo / Under Review",
                description = "Profile photo is blurred, please verify identity before search visibility.",
                status = "PENDING",
                actionTaken = "None",
                createdAt = now - 7200000
            )
        )
    }

    fun getInitialAuditLogs(): List<AuditLogEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            AuditLogEntity(
                id = "log_001",
                actorRole = "SuperAdmin",
                action = "APPROVE_VERIFICATION",
                targetProfileId = "user_me",
                targetName = "Rahul Rathod",
                details = "Aadhaar / Passport document verified against declared DOB 1997-04-15 (Age 29, 18+ compliant).",
                timestamp = now - 86400000 * 3
            ),
            AuditLogEntity(
                id = "log_002",
                actorRole = "Moderator",
                action = "SECURITY_CHECK",
                targetProfileId = "prof_001",
                targetName = "Dr. Priyanka Pawar",
                details = "Medical council registration credential checked. Verified badge awarded.",
                timestamp = now - 86400000 * 2
            )
        )
    }
}
