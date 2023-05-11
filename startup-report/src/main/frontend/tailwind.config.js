/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["../resources/**/*.html", "src/index.js"],
  safelist: [
    {
      pattern: /bg-(blue|green|yellow)-([1-5]00)/,
      variants: ['hover']
    }
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}

