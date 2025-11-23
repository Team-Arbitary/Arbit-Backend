#!/bin/bash

# Quick setup script for Render deployment
echo "🚀 Render.com Deployment Setup"
echo "================================"
echo ""

# Check if render.yaml exists
if [ ! -f "render.yaml" ]; then
    echo "❌ render.yaml not found!"
    exit 1
fi

# Check if Dockerfile.render exists
if [ ! -f "Dockerfile.render" ]; then
    echo "❌ Dockerfile.render not found!"
    exit 1
fi

echo "✅ Deployment files found"
echo ""
echo "📋 Next Steps:"
echo ""
echo "1. Create PostgreSQL Database on Render:"
echo "   - Go to https://dashboard.render.com"
echo "   - Click 'New +' → 'PostgreSQL'"
echo "   - Name: transformer-inspection-db"
echo "   - Plan: Free"
echo "   - Click 'Create Database'"
echo "   - Copy the Internal Database URL"
echo ""
echo "2. Deploy Application:"
echo "   - Click 'New +' → 'Web Service'"
echo "   - Connect this repository"
echo "   - Environment: Docker"
echo "   - Dockerfile Path: ./Dockerfile.render"
echo "   - Plan: Free"
echo ""
echo "3. Set Environment Variables in Render Dashboard:"
echo "   SPRING_DATASOURCE_URL = [paste Internal Database URL]"
echo "   SPRING_DATASOURCE_USERNAME = postgres"
echo "   SPRING_DATASOURCE_PASSWORD = [from database URL]"
echo ""
echo "4. Deploy!"
echo ""
echo "📖 Full documentation: RENDER_DEPLOY.md"
echo ""
echo "⚠️  Important: Use INTERNAL Database URL, not External!"
echo ""
