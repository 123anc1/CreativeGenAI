# 🌟 CreativeGenAI

A Full-Stack AI Creative Generation System with Multi-Service Architecture  
基于多服务架构的 AI 创意内容生成系统（Web + AI + Image Generation）

---

# 📌 项目简介

CreativeGenAI 是一个集 **前端 Web + 后端服务 + Python AI 服务 + 图像生成模块** 于一体的智能创意生成系统。

用户可以通过 Web 页面与系统交互，调用多种 AI 模型生成文本与图像内容，并进行结果展示与管理。

该项目用于展示以下能力：

- 全栈开发能力（Frontend + Backend）
- AI 应用集成能力（DeepSeek / ZhipuAI / Ollama）
- 多服务架构设计能力（Java + Python + Web）

---

# 🧠 系统架构

Frontend (Vue3 / React)  
        │  
        ▼  
Backend Service (Spring Boot)  
        │  
        ▼  
Python AI Service (Flask / FastAPI)  
        │  
        ├── DeepSeek / ZhipuAI / Ollama  
        └── Image Generation Service  
        │  
        ▼  
Database (MySQL / Redis)  
        │  
        ▼  
File Storage (Local / OSS)  

---

# 🛠️ 技术栈

## 后端
- Spring Boot
- MyBatis
- RESTful API

## 前端
- Vue3 / React
- Pinia
- ECharts

## AI 服务
- DeepSeek API
- ZhipuAI API
- Ollama Embedding Model
- Python (Flask / FastAPI)

## 数据库
- MySQL
- Redis

## 存储
- 阿里云 OSS

## 工程化
- Docker
- Maven
- Git

---

# 📦 核心功能

## 👤 用户系统
- 用户注册 / 登录
- Token 鉴权

## 🤖 AI 内容生成
- AI 文本生成（支持多模型）
- AI 图像生成
- 多模型切换（DeepSeek / ZhipuAI / Ollama）

## 🖼️ 图像生成模块
- Blip Image Service
- 图像生成与存储
- 生成结果管理

## 📊 数据管理
- 生成历史记录
- Redis 缓存优化
- MySQL 数据持久化

## 🌐 Web 交互
- 前端页面交互
- 实时结果展示
- 数据可视化支持

---

# ⚙️ 快速开始

## 1️⃣ 后端启动

```bash
cd backend
mvn clean install
mvn spring-boot:run
