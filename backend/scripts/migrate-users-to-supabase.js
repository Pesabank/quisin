require('dotenv').config()
const { createClient } = require('@supabase/supabase-js')
const { Pool } = require('pg')

const supabase = createClient(
  process.env.SUPABASE_URL,
  process.env.SUPABASE_SERVICE_ROLE_KEY // You'll need to get this from Supabase dashboard
)

const sourcePool = new Pool({
  host: 'localhost',
  port: 5432,
  database: 'quisin_auth',
  user: 'postgres',
  password: 'postgres'
})

async function migrateUsers() {
  try {
    // Get users from source database
    const { rows } = await sourcePool.query(`
      SELECT 
        email,
        password,
        created_at,
        updated_at,
        role
      FROM users
    `)

    console.log(`Found ${rows.length} users to migrate`)

    // Migrate each user
    for (const user of rows) {
      try {
        // Create user in Supabase
        const { data, error } = await supabase.auth.admin.createUser({
          email: user.email,
          password: user.password, // Note: You might need to handle password hashing differently
          email_confirm: true,
          user_metadata: {
            role: user.role
          }
        })

        if (error) {
          console.error(`Failed to migrate user ${user.email}:`, error.message)
          continue
        }

        console.log(`Successfully migrated user ${user.email}`)

        // Set custom claims/role
        if (user.role) {
          await supabase.auth.admin.updateUserById(data.user.id, {
            app_metadata: { role: user.role }
          })
        }
      } catch (err) {
        console.error(`Error migrating user ${user.email}:`, err)
      }
    }

    console.log('Migration completed')
  } catch (err) {
    console.error('Migration failed:', err)
  } finally {
    await sourcePool.end()
  }
}

migrateUsers() 