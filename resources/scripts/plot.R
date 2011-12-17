
map_s <- function(str) {
	if ( str == "traffic.strategy.AdaptiveCarStrategy" ) {
		"adaptive"
	} else {
		"naive"
	}
}

plot_data <- function(file) {
	data <- read.table( file, header=T, sep=',')
	p <- tapply( data$Time, list(data$Hops, data$Strategy) , mean)
	l <- levels(data$Strategy)
	barplot(t(p), beside=T, legend= c( map_s(l[1]) , map_s(l[2]) ))
}
