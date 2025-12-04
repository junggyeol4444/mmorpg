package com. multiverse.combat.utils;

import org.bukkit.Location;
import org.bukkit. Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticleUtil {
    
    public static void spawnCircleParticles(Location location, Particle particle, double radius, int count) {
        if (location == null || location.getWorld() == null) return;
        
        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI * i) / count;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            
            Location particleLoc = location.clone().add(x, 0, z);
            location.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
        }
    }
    
    public static void spawnLineParticles(Location start, Location end, Particle particle, double distance) {
        if (start == null || end == null || start. getWorld() == null) return;
        if (! start.getWorld().equals(end.getWorld())) return;
        
        double length = start.distance(end);
        int steps = Math.max(1, (int) (length / distance));
        
        Vector direction = end.clone().subtract(start). toVector(). normalize();
        
        for (int i = 0; i <= steps; i++) {
            double progress = (double) i / steps;
            Location particleLoc = start.clone().add(direction.clone().multiply(length * progress));
            start.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
        }
    }
    
    public static void spawnSphereParticles(Location location, Particle particle, double radius, int count) {
        if (location == null || location.getWorld() == null) return;
        
        for (int i = 0; i < count; i++) {
            double angle1 = (2 * Math. PI * i) / count;
            double angle2 = Math.PI / 2 - (Math.PI * i) / count;
            
            double x = Math.cos(angle1) * Math.cos(angle2) * radius;
            double y = Math.sin(angle2) * radius;
            double z = Math.sin(angle1) * Math.cos(angle2) * radius;
            
            Location particleLoc = location.clone().add(x, y, z);
            location.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
        }
    }
    
    public static void spawnHelix(Location start, Location end, Particle particle, double radius, int turns) {
        if (start == null || end == null || start. getWorld() == null) return;
        if (!start.getWorld().equals(end.getWorld())) return;
        
        double length = start.distance(end);
        int steps = (int) (length * 2);
        
        Vector direction = end.clone().subtract(start).toVector().normalize();
        Vector perpendicular = getPerpendicular(direction);
        
        for (int i = 0; i <= steps; i++) {
            double progress = (double) i / steps;
            double angle = progress * turns * 2 * Math.PI;
            
            Location particleLoc = start.clone().add(direction.clone().multiply(length * progress));
            
            Vector offset = perpendicular.clone()
                .multiply(Math.cos(angle) * radius)
                .add(direction.clone().crossProduct(perpendicular).multiply(Math.sin(angle) * radius));
            
            particleLoc. add(offset);
            start.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
        }
    }
    
    public static void spawnRingParticles(Location location, Particle particle, double radius, int count, double yOffset) {
        if (location == null || location.getWorld() == null) return;
        
        Location ringLoc = location.clone().add(0, yOffset, 0);
        
        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI * i) / count;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            
            Location particleLoc = ringLoc.clone().add(x, 0, z);
            location.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
        }
    }
    
    public static void spawnExplosion(Location location, Particle particle, double radius, int density) {
        if (location == null || location.getWorld() == null) return;
        
        for (int i = 0; i < density; i++) {
            double x = (Math.random() - 0. 5) * radius * 2;
            double y = (Math.random() - 0.5) * radius * 2;
            double z = (Math.random() - 0.5) * radius * 2;
            
            Location particleLoc = location.clone().add(x, y, z);
            location.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
        }
    }
    
    private static Vector getPerpendicular(Vector vector) {
        if (Math.abs(vector.getX()) < 0.9) {
            return new Vector(1, 0, 0). crossProduct(vector). normalize();
        } else {
            return new Vector(0, 1, 0).crossProduct(vector).normalize();
        }
    }
}