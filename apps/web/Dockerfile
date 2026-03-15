FROM node:22-alpine AS builder

WORKDIR /app

ARG NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
ARG NEXT_PUBLIC_MEDIA_API_BASE_URL=http://localhost:8082/api
ARG NEXT_PUBLIC_MEDIA_PUBLIC_BASE_URL=http://localhost:8082/api/public/media
ARG NEXT_PUBLIC_SITE_URL=http://localhost:3000

ENV NEXT_PUBLIC_API_BASE_URL=$NEXT_PUBLIC_API_BASE_URL
ENV NEXT_PUBLIC_MEDIA_API_BASE_URL=$NEXT_PUBLIC_MEDIA_API_BASE_URL
ENV NEXT_PUBLIC_MEDIA_PUBLIC_BASE_URL=$NEXT_PUBLIC_MEDIA_PUBLIC_BASE_URL
ENV NEXT_PUBLIC_SITE_URL=$NEXT_PUBLIC_SITE_URL
ENV NEXT_TELEMETRY_DISABLED=1

COPY package.json package-lock.json ./
RUN npm ci

COPY . .

RUN npm run build

FROM node:22-alpine AS runner

WORKDIR /app

ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1

COPY --from=builder /app/package.json /app/package-lock.json ./
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/next.config.ts ./next.config.ts

EXPOSE 3000

CMD ["npm", "run", "start"]
