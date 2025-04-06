db.createUser(
        {
            user: "springchat",
            pwd: "springchat",
            roles: [
                {
                    role: "readWrite",
                    db: "springchat"
                }
            ]
        }
);