# Render.com Deployment Checklist

## ✅ Pre-Deployment Checklist

- [ ] Code pushed to GitHub/GitLab
- [ ] All files committed:
  - [ ] `Dockerfile.render`
  - [ ] `render.yaml`
  - [ ] Application code in `/app` directory
- [ ] Render account created at https://render.com

## 🗄️ Database Setup

- [ ] Logged into Render Dashboard
- [ ] Created PostgreSQL database:
  - [ ] Name: `transformer-inspection-db`
  - [ ] Plan: Free
  - [ ] Region: Selected
- [ ] **Copied Internal Database URL** (Critical!)
  - Format: `postgresql://user:pass@dpg-xxxx-a:5432/dbname`
  - NOT the external URL

## 🚀 Application Deployment

- [ ] Created Web Service
- [ ] Connected GitHub/GitLab repository
- [ ] Selected correct branch (`main` or `Yasiru-Dev`)
- [ ] Configured Docker environment:
  - [ ] **Dockerfile Path**: `./Dockerfile.render` ← Must be exact!
  - [ ] Plan: Free
  - [ ] Region: Same as database

## 🔧 Environment Variables Set

In Render Dashboard → Web Service → Environment:

- [ ] `SPRING_DATASOURCE_URL`
  - Value: `jdbc:postgresql://dpg-xxxx-a:5432/trasformer_inspection_db`
  - (Converted from PostgreSQL URL to JDBC format)
  
- [ ] `SPRING_DATASOURCE_USERNAME`
  - Value: `postgres` (or your database username)
  
- [ ] `SPRING_DATASOURCE_PASSWORD`
  - Value: (from database credentials)

## 🔍 Post-Deployment Verification

- [ ] Build completed successfully (check Logs tab)
- [ ] Application started (look for "Started SoftwareDesignCompetitionApplication")
- [ ] Health check passing:
  ```
  curl https://your-app.onrender.com/transformer-thermal-inspection/actuator/health
  ```
- [ ] No database connection errors in logs
- [ ] Can access the application URL

## 📝 Application URL

Your app will be at:
```
https://transformer-inspection-app.onrender.com
```

Health endpoint:
```
https://transformer-inspection-app.onrender.com/transformer-thermal-inspection/actuator/health
```

## 🐛 Troubleshooting

If deployment fails:

### Application Exits Early
- [ ] Verified using `Dockerfile.render` (not `.devcontainer/Dockerfile`)
- [ ] Checked all environment variables are set
- [ ] Verified database URL is **Internal** URL

### Build Fails
- [ ] Maven dependencies accessible
- [ ] `app/pom.xml` file exists and valid
- [ ] Build logs checked for specific error

### Health Check Fails
- [ ] Waited 60 seconds for startup
- [ ] Checked logs for application errors
- [ ] Verified database connection in logs

### Database Connection Error
- [ ] Using JDBC format: `jdbc:postgresql://...`
- [ ] Using **Internal** database URL (not External)
- [ ] Database is in "Available" status
- [ ] Username and password correct

## 📊 Expected Behavior (Free Tier)

✅ **Normal**:
- First deploy takes 3-5 minutes
- App spins down after 15 minutes inactivity
- First request after spin-down takes 30-60 seconds
- 512MB RAM usage

❌ **Issues**:
- "Application exited early" = check this list
- "Health check failed" = wait longer or check logs
- "Out of memory" = already optimized, may need upgrade

## 🎯 Success Criteria

When everything works:
- ✅ Deployment shows "Live"
- ✅ Health check returns `{"status":"UP"}`
- ✅ Logs show no errors
- ✅ Can access application endpoints
- ✅ Database operations work

## 📚 Additional Resources

- Full guide: `RENDER_DEPLOY.md`
- Fix documentation: `RENDER_FIX.md`
- Docker guide: `DOCKER.md`
- Quick setup: Run `./render-setup.sh`

## 💡 Pro Tips

1. **Always use Internal Database URL** - External URLs won't work from the app
2. **Check logs first** - Most issues show up in logs
3. **Wait for startup** - Free tier can take 30-60 seconds
4. **Test locally first** - Use `docker-compose up` to test before deploying
5. **Save your environment variables** - Keep a secure copy

---

## Need Help?

1. Check `RENDER_DEPLOY.md` for detailed troubleshooting
2. View logs in Render Dashboard
3. Visit Render community: https://community.render.com/
4. Review application logs for specific errors

---

**Current Status**: [ ] Not Started / [ ] In Progress / [ ] Completed ✅
