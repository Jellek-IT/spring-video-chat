db.createUser({
  user: "springchat_notification",
  pwd: process.env.SPRINGCHAT_NOTIFICATION_DB_PASSWORD,
  roles: [
    {
      role: "readWrite",
      db: process.env.MONGO_INITDB_DATABASE,
    },
  ],
});
