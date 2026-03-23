/**
 * 登录页背景动画逻辑
 * 管理动画粒子和视觉效果
 */

import { ref, onMounted, onUnmounted } from 'vue'

interface Particle {
  x: number
  y: number
  size: number
  speed: number
}

const particles = ref<Particle[]>([])
let particleInterval: number | undefined

const generateParticles = () => {
  const container = document.querySelector('.particle-container') as HTMLElement
  if (!container) return

  particles.value = []
  const count = 20

  for (let i = 0; i < count; i++) {
    particles.value.push({
      x: Math.random() * 100,
      y: Math.random() * 100,
      size: Math.random() * 4 + 1,
      speed: Math.random() * 0.5 + 0.2
    })
  }

  renderParticles()
}

const renderParticles = () => {
  const container = document.querySelector('.particle-container') as HTMLElement
  if (!container) return

  container.innerHTML = ''

  particles.value.forEach((particle) => {
    const div = document.createElement('div')
    div.className = 'particle'
    div.style.cssText = `
      left: ${particle.x}%;
      top: ${particle.y}%;
      width: ${particle.size}px;
      height: ${particle.size}px;
      opacity: ${0.3 + Math.random() * 0.4};
      animation-duration: ${3 / particle.speed}s;
    `
    container.appendChild(div)
  })
}

const animateParticles = () => {
  particles.value.forEach(particle => {
    particle.y += particle.speed
    if (particle.y > 100) {
      particle.y = -5
      particle.x = Math.random() * 100
    }
  })
  renderParticles()
}

const startAnimation = () => {
  generateParticles()
  particleInterval = window.setInterval(animateParticles, 50)
}

const stopAnimation = () => {
  if (particleInterval) {
    clearInterval(particleInterval)
  }
}

// 生命周期
onMounted(() => {
  startAnimation()
})

onUnmounted(() => {
  stopAnimation()
})

export { particles, generateParticles, renderParticles, animateParticles }
