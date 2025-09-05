-- Create a keyspace
CREATE KEYSPACE IF NOT EXISTS notifications WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '1' };

-- Create a table

CREATE TABLE IF NOT EXISTS notifications.user_notifications (
    user_id text,
    created_at timestamp,
    notification_id text,
    message text,
    PRIMARY KEY ((user_id), created_at, notification_id)
);

-- Insert some data
