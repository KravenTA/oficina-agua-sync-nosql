-- HU-55: estructura y datos de la instancia MySQL de origen.
-- Generado ejecutando las migraciones y los seeders del monolito (php spark migrate y
-- php spark db:seed DatabaseSeeder) y exportado con mysqldump. El monolito no se modifica.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

/*!50503 SET NAMES utf8mb4 */;
DROP TABLE IF EXISTS `auditoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditoria` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `tabla` varchar(30) COLLATE utf8mb4_general_ci NOT NULL,
  `registro_id` int unsigned NOT NULL,
  `accion` enum('alta','edicion','baja') COLLATE utf8mb4_general_ci NOT NULL,
  `usuario_id` int unsigned DEFAULT NULL,
  `datos_anteriores` text COLLATE utf8mb4_general_ci,
  `datos_nuevos` text COLLATE utf8mb4_general_ci,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tabla_registro_id` (`tabla`,`registro_id`),
  KEY `usuario_id` (`usuario_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `auditoria` DISABLE KEYS */;
/*!40000 ALTER TABLE `auditoria` ENABLE KEYS */;
DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `direccion` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(150) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `dpi` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `nit` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `foto_vivienda` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `recibo_luz` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `observaciones` varchar(150) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (1,'Ana Lucia Moran','23000000','Aldea La Majada, Jutiapa',NULL,NULL,NULL,NULL,NULL,0,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (2,'Carlos Perez Ruiz','23007919','Aldea El Zapote, Jutiapa','carlos2@correo.test',NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (3,'Maria Jose Aguilar','23015838','Barrio El Centro, Jutiapa','maria3@correo.test',NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (4,'Jorge Ramirez Lopez','23023757','Caserio Los Encuentros, Jutiapa',NULL,NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (5,'Sofia Hernandez','23031676','Aldea San Antonio, Jutiapa','sofia5@correo.test',NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (6,'Luis Alberto Cruz','23039595','Aldea La Majada, Jutiapa','luis6@correo.test',NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (7,'Elena Vasquez','23047514','Aldea El Zapote, Jutiapa',NULL,NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (8,'Pedro Antonio Diaz','23055433','Barrio El Centro, Jutiapa','pedro8@correo.test',NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (9,'Claudia Morales','23063352','Caserio Los Encuentros, Jutiapa','claudia9@correo.test',NULL,NULL,NULL,NULL,0,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (10,'Roberto Gonzalez','23071271','Aldea San Antonio, Jutiapa',NULL,NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (11,'Andrea Castillo','23079190','Aldea La Majada, Jutiapa','andrea11@correo.test',NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (12,'Miguel Angel Reyes','23087109','Aldea El Zapote, Jutiapa','miguel12@correo.test',NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (13,'Patricia Solis','23095028','Barrio El Centro, Jutiapa',NULL,NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (14,'Fernando Estrada','23102947','Caserio Los Encuentros, Jutiapa','fernando14@correo.test',NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (15,'Gabriela Mendez','23110866','Aldea San Antonio, Jutiapa','gabriela15@correo.test',NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (16,'Hector Villatoro','23118785','Aldea La Majada, Jutiapa',NULL,NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (17,'Silvia Corado','23126704','Aldea El Zapote, Jutiapa','silvia17@correo.test',NULL,NULL,NULL,NULL,0,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (18,'Mario Alvarado','23134623','Barrio El Centro, Jutiapa','mario18@correo.test',NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (19,'Rosa Maria Chacon','23142542','Caserio Los Encuentros, Jutiapa',NULL,NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (20,'Julio Cesar Barrios','23150461','Aldea San Antonio, Jutiapa','julio20@correo.test',NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (21,'Marta Alicia Pineda','23158380','Aldea La Majada, Jutiapa','marta21@correo.test',NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (22,'Oscar Recinos','23166299','Aldea El Zapote, Jutiapa',NULL,NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (23,'Karla Sandoval','23174218','Barrio El Centro, Jutiapa','karla23@correo.test',NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (24,'Byron Estuardo Lima','23182137','Caserio Los Encuentros, Jutiapa','byron24@correo.test',NULL,NULL,NULL,NULL,1,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `clientes` (`id`, `nombre`, `telefono`, `direccion`, `email`, `dpi`, `nit`, `foto_vivienda`, `recibo_luz`, `activo`, `observaciones`, `created_at`, `updated_at`) VALUES (25,'Vilma Guzman','23190056','Aldea San Antonio, Jutiapa',NULL,NULL,NULL,NULL,NULL,0,NULL,'2026-10-21 05:07:36','2026-10-21 05:07:36');
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
DROP TABLE IF EXISTS `contadores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contadores` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `servicio_id` int unsigned NOT NULL,
  `numero_serie` varchar(30) COLLATE utf8mb4_general_ci NOT NULL,
  `tipo_servicio` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `lectura_inicial` decimal(10,2) NOT NULL DEFAULT '0.00',
  `fecha_instalacion` date DEFAULT NULL,
  `fecha_retiro` date DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_serie` (`numero_serie`),
  KEY `contadores_servicio_id_foreign` (`servicio_id`),
  CONSTRAINT `contadores_servicio_id_foreign` FOREIGN KEY (`servicio_id`) REFERENCES `servicios` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `contadores` DISABLE KEYS */;
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (1,1,'CTR-0001','cuarto_paja',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (2,2,'CTR-0002','media_paja',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (3,3,'CTR-0003','paja_completa',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (4,4,'CTR-0004','cuarto_paja',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (5,5,'CTR-0005','media_paja',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (6,6,'CTR-0006','paja_completa',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (7,7,'CTR-0007','cuarto_paja',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (8,8,'CTR-0008','media_paja',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (9,9,'CTR-0009','paja_completa',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (10,10,'CTR-0010','cuarto_paja',0.00,'2026-01-20','2026-06-30',0,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (11,11,'CTR-0011','media_paja',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (12,12,'CTR-0012','paja_completa',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (13,13,'CTR-0013','cuarto_paja',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (14,14,'CTR-0014','media_paja',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (15,15,'CTR-0015','paja_completa',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (16,16,'CTR-0016','cuarto_paja',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (17,17,'CTR-0017','media_paja',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (18,18,'CTR-0018','paja_completa',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (19,19,'CTR-0019','cuarto_paja',0.00,'2026-01-20','2026-06-30',0,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (20,20,'CTR-0020','media_paja',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (21,21,'CTR-0021','paja_completa',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (22,22,'CTR-0022','cuarto_paja',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (23,23,'CTR-0023','media_paja',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (24,24,'CTR-0024','paja_completa',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `contadores` (`id`, `servicio_id`, `numero_serie`, `tipo_servicio`, `lectura_inicial`, `fecha_instalacion`, `fecha_retiro`, `activo`, `created_at`, `updated_at`) VALUES (25,25,'CTR-0025','cuarto_paja',0.00,'2026-01-20',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
/*!40000 ALTER TABLE `contadores` ENABLE KEYS */;
DROP TABLE IF EXISTS `lecturas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lecturas` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `servicio_id` int unsigned NOT NULL,
  `contador_id` int unsigned NOT NULL,
  `periodo_id` int unsigned NOT NULL,
  `tarifa_id` int unsigned NOT NULL,
  `lector_usuario_id` int unsigned NOT NULL,
  `lectura_anterior` decimal(10,2) NOT NULL,
  `lectura_actual` decimal(10,2) NOT NULL,
  `consumo` decimal(10,2) DEFAULT NULL,
  `monto` decimal(10,2) DEFAULT NULL,
  `fecha_lectura` date NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lecturas_contador_periodo` (`contador_id`,`periodo_id`),
  KEY `lecturas_servicio_id_foreign` (`servicio_id`),
  KEY `lecturas_periodo_id_foreign` (`periodo_id`),
  KEY `lecturas_tarifa_id_foreign` (`tarifa_id`),
  KEY `lecturas_lector_usuario_id_foreign` (`lector_usuario_id`),
  CONSTRAINT `lecturas_contador_id_foreign` FOREIGN KEY (`contador_id`) REFERENCES `contadores` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `lecturas_lector_usuario_id_foreign` FOREIGN KEY (`lector_usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `lecturas_periodo_id_foreign` FOREIGN KEY (`periodo_id`) REFERENCES `periodos` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `lecturas_servicio_id_foreign` FOREIGN KEY (`servicio_id`) REFERENCES `servicios` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `lecturas_tarifa_id_foreign` FOREIGN KEY (`tarifa_id`) REFERENCES `tarifas` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `lecturas` DISABLE KEYS */;
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (1,1,1,1,1,3,100.00,108.00,8.00,15.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (2,1,1,2,1,3,108.00,117.00,9.00,15.00,'2026-08-26','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (3,2,2,1,2,3,103.00,112.00,9.00,25.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (4,3,3,1,3,3,106.00,116.00,10.00,45.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (5,3,3,2,3,3,116.00,127.00,11.00,45.00,'2026-08-26','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (6,4,4,1,1,3,109.00,120.00,11.00,18.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (7,5,5,1,2,3,112.00,124.00,12.00,25.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (8,5,5,2,2,3,124.00,133.00,9.00,25.00,'2026-08-26','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (9,6,6,1,3,3,115.00,123.00,8.00,45.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (10,7,7,1,1,3,118.00,127.00,9.00,15.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (11,7,7,2,1,3,127.00,138.00,11.00,18.00,'2026-08-26','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (12,8,8,1,2,3,121.00,131.00,10.00,25.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (13,9,9,1,3,3,124.00,135.00,11.00,45.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (14,9,9,2,3,3,135.00,144.00,9.00,45.00,'2026-08-26','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (15,11,11,1,2,3,127.00,139.00,12.00,25.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (16,13,13,1,1,3,130.00,138.00,8.00,15.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (17,13,13,2,1,3,138.00,149.00,11.00,18.00,'2026-08-26','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (18,14,14,1,2,3,133.00,142.00,9.00,25.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (19,15,15,1,3,3,136.00,146.00,10.00,45.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (20,15,15,2,3,3,146.00,155.00,9.00,45.00,'2026-08-26','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (21,16,16,1,1,3,139.00,150.00,11.00,18.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (22,17,17,1,2,3,142.00,154.00,12.00,25.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (23,17,17,2,2,3,154.00,165.00,11.00,25.00,'2026-08-26','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (24,18,18,1,3,3,145.00,153.00,8.00,45.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (25,20,20,1,2,3,148.00,157.00,9.00,25.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (26,20,20,2,2,3,157.00,166.00,9.00,25.00,'2026-08-26','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (27,21,21,1,3,3,151.00,161.00,10.00,45.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (28,22,22,1,1,3,154.00,165.00,11.00,18.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (29,22,22,2,1,3,165.00,176.00,11.00,18.00,'2026-08-26','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (30,24,24,1,3,3,157.00,169.00,12.00,45.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (31,25,25,1,1,3,160.00,168.00,8.00,15.00,'2026-07-28','2026-10-21 05:07:37','2026-10-21 05:07:37');
INSERT INTO `lecturas` (`id`, `servicio_id`, `contador_id`, `periodo_id`, `tarifa_id`, `lector_usuario_id`, `lectura_anterior`, `lectura_actual`, `consumo`, `monto`, `fecha_lectura`, `created_at`, `updated_at`) VALUES (32,25,25,2,1,3,168.00,177.00,9.00,15.00,'2026-08-26','2026-10-21 05:07:37','2026-10-21 05:07:37');
/*!40000 ALTER TABLE `lecturas` ENABLE KEYS */;
DROP TABLE IF EXISTS `log_errores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `log_errores` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `nivel` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `mensaje` text COLLATE utf8mb4_general_ci NOT NULL,
  `archivo` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `linea` int unsigned DEFAULT NULL,
  `usuario_id` int unsigned DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `log_errores` DISABLE KEYS */;
/*!40000 ALTER TABLE `log_errores` ENABLE KEYS */;
DROP TABLE IF EXISTS `migrations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `migrations` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `version` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `class` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `group` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `namespace` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `time` int NOT NULL,
  `batch` int unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `migrations` DISABLE KEYS */;
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (1,'2026-08-26-000001','App\\Database\\Migrations\\CreateClientesTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (2,'2026-08-26-000002','App\\Database\\Migrations\\CreateSectoresTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (3,'2026-08-26-000003','App\\Database\\Migrations\\CreateServiciosTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (4,'2026-08-26-000004','App\\Database\\Migrations\\CreateContadoresTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (5,'2026-08-26-000005','App\\Database\\Migrations\\CreatePeriodosTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (6,'2026-08-26-000006','App\\Database\\Migrations\\CreateTarifasTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (7,'2026-08-26-000007','App\\Database\\Migrations\\CreateRolesTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (8,'2026-08-26-000008','App\\Database\\Migrations\\CreateUsuariosTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (9,'2026-08-26-000009','App\\Database\\Migrations\\CreateLecturasTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (10,'2026-08-26-000010','App\\Database\\Migrations\\CreateRecibosTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (11,'2026-08-26-000011','App\\Database\\Migrations\\CreateDepositosTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (12,'2026-08-26-000012','App\\Database\\Migrations\\CreatePagosTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (13,'2026-08-28-000001','App\\Database\\Migrations\\AddDireccionToClientesTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (14,'2026-09-01-000001','App\\Database\\Migrations\\AddDocumentosClienteTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (15,'2026-09-01-000002','App\\Database\\Migrations\\AddTipoTuberiaCapacidadContadoresTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (16,'2026-09-01-000003','App\\Database\\Migrations\\RenameTipoTuberiaToTipoPajaContadoresTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (17,'2026-09-01-000004','App\\Database\\Migrations\\RenameTipoPajaToTipoServicioContadoresTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (18,'2026-09-01-000005','App\\Database\\Migrations\\AddTipoVolumenTarifasTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (19,'2026-09-02-000001','App\\Database\\Migrations\\CreateLogErroresTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (20,'2026-09-02-000002','App\\Database\\Migrations\\FixContadoresTipoServicioColumn','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (21,'2026-09-02-164844','App\\Database\\Migrations\\AddConsumoMontoToLecturasTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (22,'2026-09-02-164922','App\\Database\\Migrations\\AddUniqueContadorPeriodoToLecturasTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (23,'2026-09-03-000001','App\\Database\\Migrations\\CreateAuditoriaTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (24,'2026-09-03-162000','App\\Database\\Migrations\\AddLecturaIdToPagosTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (25,'2026-09-07-144500','App\\Database\\Migrations\\DropDepositosTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (26,'2026-09-09-000002','App\\Database\\Migrations\\AddFechaVencimientoToRecibosTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (27,'2026-09-09-100100','App\\Database\\Migrations\\AddNitToClientesTable','default','App',1792559256,1);
INSERT INTO `migrations` (`id`, `version`, `class`, `group`, `namespace`, `time`, `batch`) VALUES (28,'2026-09-09-100200','App\\Database\\Migrations\\BackfillFechaVencimientoRecibos','default','App',1792559256,1);
/*!40000 ALTER TABLE `migrations` ENABLE KEYS */;
DROP TABLE IF EXISTS `pagos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagos` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `lectura_id` int unsigned DEFAULT NULL,
  `recibo_id` int unsigned DEFAULT NULL,
  `usuario_id` int unsigned NOT NULL,
  `monto` decimal(10,2) NOT NULL,
  `fecha_pago` datetime NOT NULL,
  `metodo` varchar(30) COLLATE utf8mb4_general_ci NOT NULL,
  `referencia` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pagos_lectura` (`lectura_id`),
  KEY `pagos_recibo_id_foreign` (`recibo_id`),
  KEY `pagos_usuario_id_foreign` (`usuario_id`),
  CONSTRAINT `pagos_lectura_id_foreign` FOREIGN KEY (`lectura_id`) REFERENCES `lecturas` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `pagos_recibo_id_foreign` FOREIGN KEY (`recibo_id`) REFERENCES `recibos` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `pagos_usuario_id_foreign` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `pagos` DISABLE KEYS */;
/*!40000 ALTER TABLE `pagos` ENABLE KEYS */;
DROP TABLE IF EXISTS `periodos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `periodos` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `anio` int NOT NULL,
  `mes` int NOT NULL,
  `estado` enum('abierto','cerrado') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'abierto',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `anio_mes` (`anio`,`mes`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `periodos` DISABLE KEYS */;
INSERT INTO `periodos` (`id`, `anio`, `mes`, `estado`, `created_at`, `updated_at`) VALUES (1,2026,7,'cerrado','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `periodos` (`id`, `anio`, `mes`, `estado`, `created_at`, `updated_at`) VALUES (2,2026,8,'abierto','2026-10-21 05:07:36','2026-10-21 05:07:36');
/*!40000 ALTER TABLE `periodos` ENABLE KEYS */;
DROP TABLE IF EXISTS `recibos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recibos` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `lectura_id` int unsigned NOT NULL,
  `numero` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `fecha_emision` datetime NOT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  `monto_consumo` decimal(10,2) NOT NULL,
  `monto_adicional` decimal(10,2) NOT NULL DEFAULT '0.00',
  `concepto_adicional` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `total` decimal(10,2) NOT NULL,
  `estado` enum('pendiente','pagado','vencido','anulado') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'pendiente',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero` (`numero`),
  KEY `recibos_lectura_id_foreign` (`lectura_id`),
  CONSTRAINT `recibos_lectura_id_foreign` FOREIGN KEY (`lectura_id`) REFERENCES `lecturas` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `recibos` DISABLE KEYS */;
/*!40000 ALTER TABLE `recibos` ENABLE KEYS */;
DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` (`id`, `nombre`, `activo`, `created_at`, `updated_at`) VALUES (1,'Administrador',1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `roles` (`id`, `nombre`, `activo`, `created_at`, `updated_at`) VALUES (2,'Secretaria',1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `roles` (`id`, `nombre`, `activo`, `created_at`, `updated_at`) VALUES (3,'Lector',1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
DROP TABLE IF EXISTS `sectores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sectores` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `sectores` DISABLE KEYS */;
INSERT INTO `sectores` (`id`, `nombre`, `activo`, `created_at`, `updated_at`) VALUES (1,'Aldea La Majada',1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `sectores` (`id`, `nombre`, `activo`, `created_at`, `updated_at`) VALUES (2,'Aldea El Zapote',1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `sectores` (`id`, `nombre`, `activo`, `created_at`, `updated_at`) VALUES (3,'Barrio El Centro',1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `sectores` (`id`, `nombre`, `activo`, `created_at`, `updated_at`) VALUES (4,'Caserio Los Encuentros',1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `sectores` (`id`, `nombre`, `activo`, `created_at`, `updated_at`) VALUES (5,'Aldea San Antonio',1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
/*!40000 ALTER TABLE `sectores` ENABLE KEYS */;
DROP TABLE IF EXISTS `servicios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servicios` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `cliente_id` int unsigned NOT NULL,
  `sector_id` int unsigned NOT NULL,
  `codigo` varchar(30) COLLATE utf8mb4_general_ci NOT NULL,
  `direccion` varchar(150) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha_alta` date DEFAULT NULL,
  `estado` enum('activo','suspendido','cancelado') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'activo',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`),
  KEY `servicios_cliente_id_foreign` (`cliente_id`),
  KEY `servicios_sector_id_foreign` (`sector_id`),
  CONSTRAINT `servicios_cliente_id_foreign` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `servicios_sector_id_foreign` FOREIGN KEY (`sector_id`) REFERENCES `sectores` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `servicios` DISABLE KEYS */;
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (1,1,1,'SRV-0001','Aldea La Majada, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (2,2,2,'SRV-0002','Aldea El Zapote, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (3,3,3,'SRV-0003','Barrio El Centro, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (4,4,4,'SRV-0004','Caserio Los Encuentros, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (5,5,5,'SRV-0005','Aldea San Antonio, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (6,6,1,'SRV-0006','Aldea La Majada, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (7,7,2,'SRV-0007','Aldea El Zapote, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (8,8,3,'SRV-0008','Barrio El Centro, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (9,9,4,'SRV-0009','Caserio Los Encuentros, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (10,10,5,'SRV-0010','Aldea San Antonio, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (11,11,1,'SRV-0011','Aldea La Majada, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (12,12,2,'SRV-0012','Aldea El Zapote, Jutiapa','2026-01-15','suspendido','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (13,13,3,'SRV-0013','Barrio El Centro, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (14,14,4,'SRV-0014','Caserio Los Encuentros, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (15,15,5,'SRV-0015','Aldea San Antonio, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (16,16,1,'SRV-0016','Aldea La Majada, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (17,17,2,'SRV-0017','Aldea El Zapote, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (18,18,3,'SRV-0018','Barrio El Centro, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (19,19,4,'SRV-0019','Caserio Los Encuentros, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (20,20,5,'SRV-0020','Aldea San Antonio, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (21,21,1,'SRV-0021','Aldea La Majada, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (22,22,2,'SRV-0022','Aldea El Zapote, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (23,23,3,'SRV-0023','Barrio El Centro, Jutiapa','2026-01-15','suspendido','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (24,24,4,'SRV-0024','Caserio Los Encuentros, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `servicios` (`id`, `cliente_id`, `sector_id`, `codigo`, `direccion`, `fecha_alta`, `estado`, `created_at`, `updated_at`) VALUES (25,25,5,'SRV-0025','Aldea San Antonio, Jutiapa','2026-01-15','activo','2026-10-21 05:07:36','2026-10-21 05:07:36');
/*!40000 ALTER TABLE `servicios` ENABLE KEYS */;
DROP TABLE IF EXISTS `tarifas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tarifas` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `tipo` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `volumen_incluido_litros` int unsigned DEFAULT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `cuota_minima` decimal(10,2) NOT NULL DEFAULT '0.00',
  `vigente_desde` date NOT NULL,
  `vigente_hasta` date DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `tarifas` DISABLE KEYS */;
INSERT INTO `tarifas` (`id`, `tipo`, `volumen_incluido_litros`, `precio_unitario`, `cuota_minima`, `vigente_desde`, `vigente_hasta`, `activo`, `created_at`, `updated_at`) VALUES (1,'cuarto_paja',10000,2.50,15.00,'2026-01-01',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `tarifas` (`id`, `tipo`, `volumen_incluido_litros`, `precio_unitario`, `cuota_minima`, `vigente_desde`, `vigente_hasta`, `activo`, `created_at`, `updated_at`) VALUES (2,'media_paja',20000,2.50,25.00,'2026-01-01',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `tarifas` (`id`, `tipo`, `volumen_incluido_litros`, `precio_unitario`, `cuota_minima`, `vigente_desde`, `vigente_hasta`, `activo`, `created_at`, `updated_at`) VALUES (3,'paja_completa',40000,2.50,45.00,'2026-01-01',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `tarifas` (`id`, `tipo`, `volumen_incluido_litros`, `precio_unitario`, `cuota_minima`, `vigente_desde`, `vigente_hasta`, `activo`, `created_at`, `updated_at`) VALUES (4,'exceso',0,3.00,0.00,'2026-01-01',NULL,1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
/*!40000 ALTER TABLE `tarifas` ENABLE KEYS */;
DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `rol_id` int unsigned NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(150) COLLATE utf8mb4_general_ci NOT NULL,
  `password_hash` varchar(150) COLLATE utf8mb4_general_ci NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `usuarios_rol_id_foreign` (`rol_id`),
  CONSTRAINT `usuarios_rol_id_foreign` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` (`id`, `rol_id`, `nombre`, `email`, `password_hash`, `activo`, `created_at`, `updated_at`) VALUES (1,1,'Administrador del sistema','admin@oficinaagua.test','no-aplica',1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `usuarios` (`id`, `rol_id`, `nombre`, `email`, `password_hash`, `activo`, `created_at`, `updated_at`) VALUES (2,2,'Secretaria de oficina','secretaria@oficinaagua.test','no-aplica',1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
INSERT INTO `usuarios` (`id`, `rol_id`, `nombre`, `email`, `password_hash`, `activo`, `created_at`, `updated_at`) VALUES (3,3,'Lector de campo','lector@oficinaagua.test','no-aplica',1,'2026-10-21 05:07:36','2026-10-21 05:07:36');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;

SET FOREIGN_KEY_CHECKS = 1;
