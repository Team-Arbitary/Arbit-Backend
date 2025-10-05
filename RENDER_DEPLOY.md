# Deploying to Render.com

This guide explains how to deploy the Transformer Inspection Backend to Render.com.

## Prerequisites

- A [Render.com](https://render.com) account (free tier available)
- Your code pushed to GitHub/GitLab
- A PostgreSQL database (can be created on Render)

## Step 1: Create PostgreSQL Database

1. Log in to [Render Dashboard](https://dashboard.render.com)
2. Click **New +** → **PostgreSQL**
3. Configure:
   - **Name**: `transformer-inspection-db`
   - **Database**: `trasformer_inspection_db`
   - **User**: `postgres` (or any name)
   - **Region**: Choose closest to you
   - **Plan**: Free
4. Click **Create Database**
5. **IMPORTANT**: Copy the **Internal Database URL** (it looks like: `postgresql://user:pass@hostname:5432/dbname`)

## Step 2: Deploy Application

### Option A: Using Render Dashboard (Easiest)

1. Click **New +** → **Web Service**
2. Connect your GitHub/GitLab repository
3. Configure:
   - **Name**: `transformer-inspection-app`
   - **Environment**: `Docker`
   - **Region**: Same as database
   - **Branch**: `main` or `Yasiru-Dev`
   - **Dockerfile Path**: `./Dockerfile.render`
   - **Plan**: Free

4. Add Environment Variables:
   ```
   SPRING_DATASOURCE_URL = [paste Internal Database URL from Step 1]
   SPRING_DATASOURCE_USERNAME = postgres
   SPRING_DATASOURCE_PASSWORD = [from database URL]
   PORT = 10000
   ```

5. Click **Create Web Service**

### Option B: Using render.yaml (Infrastructure as Code)

1. Create a PostgreSQL database as described in Step 1
2. Update `render.yaml` with your database credentials
3. Push your code to GitHub
4. In Render Dashboard: **New +** → **Blueprint**
5. Connect repository and deploy

## Step 3: Configure Health Check

Render will automatically check: `/transformer-thermal-inspection/actuator/health`

If it fails, the deployment will stop. This is normal - wait for the app to start (30-60 seconds).

## Environment Variables Reference

| Variable | Value | Notes |
|----------|-------|-------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://hostname:5432/dbname` | From Render database |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `your-password` | Database password |
| `PORT` | `10000` | Render's default port |
| `SPRING_PROFILES_ACTIVE` | `prod` | Optional: Spring profile |

## Troubleshooting

### Application exits early

**Cause**: Missing environment variables or incorrect database connection

**Solution**:
1. Check logs in Render Dashboard → Logs tab
2. Verify all environment variables are set
3. Ensure database URL is the **Internal URL**, not External
4. Check database is running: Status should be "Available"

### Database connection timeout

**Cause**: Using External Database URL instead of Internal

**Solution**: 
- Use the **Internal Database URL** (starts with `postgresql://`)
- Format: `jdbc:postgresql://dpg-xxx-a:5432/dbname`

### Port binding error

**Cause**: Application not using PORT environment variable

**Solution**: `Dockerfile.render` is configured to use PORT variable automatically

### Out of memory

**Cause**: Free tier has 512MB RAM limit

**Solution**: Already optimized in `Dockerfile.render` with `-Xms256m -Xmx512m`

### Build fails

**Cause**: Maven dependencies timeout or build exceeds time limit

**Solution**: 
1. Check if all dependencies are accessible
2. Consider caching dependencies
3. Upgrade to paid plan for longer build time

## Render Free Tier Limitations

- ⏱️ Spins down after 15 minutes of inactivity
- 🐌 First request after spin-down takes 30-60 seconds
- 💾 512 MB RAM
- 💿 No persistent disk (use database for all data)
- 🔄 Automatic deploys on git push

## Upgrading for Production

For production use, consider:

1. **Upgrade to paid plan** ($7/month)
   - No spin down
   - More RAM (1GB+)
   - Better performance

2. **Use managed PostgreSQL**
   - Better reliability
   - Automatic backups
   - More storage

3. **Add monitoring**
   - Set up health check alerts
   - Monitor application metrics
   - Configure logging

4. **Custom domain**
   - Add your domain in Render settings
   - Configure SSL (automatic with Render)

## Access Your Application

After deployment:
- **Application URL**: `https://transformer-inspection-app.onrender.com`
- **Health Check**: `https://transformer-inspection-app.onrender.com/transformer-thermal-inspection/actuator/health`

## Logs

View logs in Render Dashboard:
1. Go to your Web Service
2. Click **Logs** tab
3. Real-time logs will appear

## Updating Application

Automatic deployment on git push:
```bash
git add .
git commit -m "Update application"
git push origin main
```

Render will automatically rebuild and redeploy.

## Manual Deployment

In Render Dashboard:
1. Go to your Web Service
2. Click **Manual Deploy** → **Deploy latest commit**

## Cost Estimate

Free tier is perfect for development and testing:
- PostgreSQL: Free (1GB storage, expires after 90 days)
- Web Service: Free (with limitations)

Paid production setup (~$25/month):
- PostgreSQL: $7/month (10GB storage)
- Web Service: $7/month (512MB RAM)
- Additional services as needed

## Support

- [Render Documentation](https://render.com/docs)
- [Render Community](https://community.render.com/)
- [Spring Boot on Render](https://render.com/docs/deploy-spring-boot)
